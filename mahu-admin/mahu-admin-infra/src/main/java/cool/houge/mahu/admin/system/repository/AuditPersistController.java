package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.entity.Auditable;
import cool.houge.mahu.util.Metadata;
import io.ebean.PersistenceIOException;
import io.ebean.bean.BeanDiffVisitor;
import io.ebean.event.BeanPersistAdapter;
import io.ebean.event.BeanPersistRequest;
import io.ebean.event.changelog.ChangeType;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.api.json.SpiJsonWriter;
import io.ebeaninternal.server.core.PersistRequestBean;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.deploy.BeanProperty;
import io.ebeaninternal.server.deploy.BeanPropertyAssocOne;
import io.ebeaninternal.server.util.ArrayStack;
import io.helidon.common.LazyValue;
import io.helidon.common.context.Contexts;
import io.hypersistence.tsid.TSID;
import java.io.IOException;
import java.io.StringWriter;

/// 管理员操作审计日志
///
/// @author ZY (kzou227@qq.com)
public class AuditPersistController extends BeanPersistAdapter {

    private static final LazyValue<TSID.Factory> AUDIT_ID_FACTORY_LV =
            LazyValue.create(() -> TSID.Factory.builder().build());

    @Override
    public boolean isRegisterFor(Class<?> cls) {
        return Auditable.class.isAssignableFrom(cls);
    }

    @Override
    public void postInsert(BeanPersistRequest<?> request) {
        saveAuditLog(ChangeType.INSERT, request);
    }

    @Override
    public void postUpdate(BeanPersistRequest<?> request) {
        saveAuditLog(ChangeType.UPDATE, request);
    }

    @Override
    public boolean preDelete(BeanPersistRequest<?> request) {
        throw new UnsupportedOperationException("禁止对数据库数据进行删除操作");
    }

    @Override
    public void postSoftDelete(BeanPersistRequest<?> request) {
        saveAuditLog(ChangeType.UPDATE, request);
    }

    void saveAuditLog(ChangeType changeType, BeanPersistRequest<?> persistRequest) {
        var request = (PersistRequestBean<?>) persistRequest;
        var server = request.server();
        var descriptor = request.descriptor();

        var entity = new AdminAuditLog().setTableName(descriptor.baseTable()).setChangeType(changeType.getCode());
        entity.setId(generateAuditId());

        var beanId = request.beanId();
        if (beanId != null) {
            entity.setDataId(beanId.toString());
        }

        if (changeType == ChangeType.UPDATE) {
            this.changeData(server, request, entity);
        }

        var currentTenantId = server.currentTenantId();
        if (currentTenantId != null) {
            entity.setDataTenantId(currentTenantId.toString());
        }

        // 当前操作用户
        var currentUser = server.config().getCurrentUserProvider().currentUser();
        entity.setAdminId((long) currentUser);

        // 获取请求的 IP 地址并设置
        var ipAddr = Contexts.context()
                .flatMap(ctx -> ctx.get(Metadata.class))
                .map(Metadata::clientAddr)
                .orElse("UNKNOWN");
        entity.setIpAddr(ipAddr);
        server.save(entity);
    }

    long generateAuditId() {
        return AUDIT_ID_FACTORY_LV.get().generate().toLong();
    }

    void changeData(SpiEbeanServer server, PersistRequestBean<?> request, AdminAuditLog auditLog) {
        var changeJson = new BeanChangeJson(server, request.descriptor(), request.isStatelessUpdate());
        request.intercept().addDirtyPropertyValues(changeJson);
        changeJson.flush();

        auditLog.setData(changeJson.newJson()).setOldData(changeJson.oldJson());
    }

    /**
     * Builds the 'new values' and 'old values' in JSON form for ChangeLog.
     */
    private static final class BeanChangeJson implements BeanDiffVisitor {

        private final StringWriter newData;
        private final StringWriter oldData;
        private final SpiJsonWriter newJson;
        private final SpiJsonWriter oldJson;
        private final ArrayStack<BeanDescriptor<?>> stack = new ArrayStack<>();
        private BeanDescriptor<?> descriptor;

        BeanChangeJson(SpiEbeanServer server, BeanDescriptor<?> descriptor, boolean statelessUpdate) {
            this.descriptor = descriptor;
            this.newData = new StringWriter(200);
            this.newJson = server.jsonExtended().createJsonWriter(newData);
            newJson.writeStartObject();
            if (statelessUpdate) {
                this.oldJson = null;
                this.oldData = null;
            } else {
                this.oldData = new StringWriter(200);
                this.oldJson = server.jsonExtended().createJsonWriter(oldData);
                oldJson.writeStartObject();
            }
        }

        @Override
        public void visit(int position, Object newVal, Object oldVal) {
            try {
                BeanProperty prop = descriptor.propertyByIndex(position);
                if (!prop.isGenerated() && prop.isDbUpdatable()) {
                    prop.jsonWriteValue(newJson, newVal);
                    if (oldJson != null) {
                        prop.jsonWriteValue(oldJson, oldVal);
                    }
                }
            } catch (IOException e) {
                throw new PersistenceIOException(e);
            }
        }

        @Override
        public void visitPush(int position) {
            stack.push(descriptor);
            BeanPropertyAssocOne<?> embedded = (BeanPropertyAssocOne<?>) descriptor.propertyByIndex(position);
            descriptor = embedded.targetDescriptor();
            newJson.writeStartObject(embedded.name());
            if (oldJson != null) {
                oldJson.writeStartObject(embedded.name());
            }
        }

        @Override
        public void visitPop() {
            newJson.writeEndObject();
            if (oldJson != null) {
                oldJson.writeEndObject();
            }
            descriptor = stack.pop();
        }

        /**
         * Flush the buffers.
         */
        void flush() {
            try {
                newJson.writeEndObject();
                newJson.flush();
                if (oldJson != null) {
                    oldJson.writeEndObject();
                    oldJson.flush();
                }
            } catch (IOException e) {
                throw new PersistenceIOException(e);
            }
        }

        /**
         * Return the new values JSON.
         */
        String newJson() {
            return newData.toString();
        }

        /**
         * Return the old values JSON.
         */
        String oldJson() {
            return oldData == null ? null : oldData.toString();
        }
    }
}
