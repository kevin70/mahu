package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.common.Metadata;
import cool.houge.mahu.entity.Auditable;
import io.ebean.bean.EntityBean;
import io.ebean.event.BeanPersistAdapter;
import io.ebean.event.BeanPersistRequest;
import io.ebean.event.changelog.ChangeType;
import io.ebeaninternal.server.core.DefaultServer;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.deploy.BeanProperty;
import io.ebeaninternal.server.deploy.BeanPropertyAssocMany;
import io.ebeaninternal.server.deploy.BeanPropertyAssocOne;
import io.helidon.common.context.Contexts;
import io.hypersistence.tsid.TSID;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

/// 管理员操作审计日志
///
/// @author ZY (kzou227@qq.com)
public class AuditPersistController extends BeanPersistAdapter {

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

    void saveAuditLog(ChangeType changeType, BeanPersistRequest<?> request) {
        var server = (DefaultServer) request.database();
        var bean = (EntityBean) request.bean();
        var descriptor = server.descriptor(bean.getClass());

        var entity = new AdminAuditLog()
                .setTableName(descriptor.baseTable())
                .setChangeType(changeType.getCode())
                .setData(changeData(server, descriptor, bean));
        entity.setId(TSID.fast().toLong());

        var id = descriptor.getId(bean);
        if (id != null) {
            entity.setDataId(id.toString());
        }

        if (descriptor.isMultiTenant()) {
            var tenantValue = descriptor.tenantProperty().getValue(bean);
            entity.setDataTenantId(tenantValue.toString());
        }

        // 获取请求的 IP 地址并设置
        var ipAddr = Contexts.context()
                .flatMap(ctx -> ctx.get(Metadata.class))
                .map(Metadata::clientAddr)
                .orElse("UNKNOWN");
        entity.setIpAddr(ipAddr);
        server.save(entity);
    }

    private String changeData(DefaultServer server, BeanDescriptor<?> descriptor, EntityBean entityBean) {
        try {
            var writer = new StringWriter(200);
            var jsonWriter = server.jsonExtended().createJsonWriter(writer);
            jsonWriter.writeStartObject();
            for (BeanProperty prop : descriptor.propertiesBaseScalar()) {
                if (prop.isGenerated()) {
                    continue;
                }
                if (prop.isImportedPrimaryKey()) {
                    continue;
                }
                if (!entityBean._ebean_intercept().isChangedProperty(prop.propertyIndex())) {
                    continue;
                }
                prop.jsonWrite(jsonWriter, entityBean);
            }
            for (BeanPropertyAssocOne<?> prop : descriptor.propertiesOne()) {
                prop.jsonWrite(jsonWriter, entityBean);
            }
            for (BeanPropertyAssocOne<?> prop : descriptor.propertiesEmbedded()) {
                prop.jsonWrite(jsonWriter, entityBean);
            }
            for (BeanPropertyAssocMany<?> prop : descriptor.propertiesMany()) {
                prop.jsonWrite(jsonWriter, entityBean);
            }
            jsonWriter.writeEndObject();
            jsonWriter.flush();
            return writer.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
