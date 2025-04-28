package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.common.Metadata;
import cool.houge.mahu.entity.Auditable;
import io.ebean.bean.EntityBean;
import io.ebean.event.BeanPersistAdapter;
import io.ebean.event.BeanPersistRequest;
import io.ebean.event.changelog.ChangeType;
import io.ebeaninternal.api.json.SpiJsonWriter;
import io.ebeaninternal.server.core.DefaultServer;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.deploy.BeanProperty;
import io.ebeaninternal.server.deploy.BeanPropertyAssocMany;
import io.helidon.common.context.Contexts;
import io.hypersistence.tsid.TSID;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.List;

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

        var entity = new AdminAuditLog().setTableName(descriptor.baseTable()).setChangeType(changeType.getCode());
        entity.setId(TSID.fast().toLong());
        this.changedData(server, descriptor, bean, entity);

        var id = descriptor.getId(bean);
        if (id != null) {
            entity.setDataId(formatDataId(server, descriptor.idProperty(), id));
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

    String formatDataId(DefaultServer server, BeanProperty idProperty, Object value) {
        try {
            var writer = new StringWriter();
            var jsonWriter = server.jsonExtended().createJsonWriter(writer);
            jsonWriter.writeStartObject();
            idProperty.jsonWriteValue(jsonWriter, value);
            jsonWriter.writeEndObject();
            jsonWriter.flush();
            return writer.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    void changedData(
            DefaultServer server, BeanDescriptor<?> descriptor, EntityBean entityBean, AdminAuditLog auditLog) {
        try {
            var oldWriter = new StringWriter(200);
            var oldJsonWriter = server.jsonExtended().createJsonWriter(oldWriter);
            oldJsonWriter.writeStartObject();

            var newWriter = new StringWriter(200);
            var newJsonWriter = server.jsonExtended().createJsonWriter(newWriter);
            newJsonWriter.writeStartObject();

            this.changedData(descriptor.propertiesBaseScalar(), entityBean, oldJsonWriter, newJsonWriter);
            this.changedData(descriptor.propertiesEmbedded(), entityBean, oldJsonWriter, newJsonWriter);
            this.changedData(descriptor.propertiesOne(), entityBean, oldJsonWriter, newJsonWriter);
            this.changedData(descriptor.propertiesMany(), entityBean, oldJsonWriter, newJsonWriter);

            newJsonWriter.writeEndObject();
            newJsonWriter.flush();

            oldJsonWriter.writeEndObject();
            oldJsonWriter.flush();

            // 设置修改数据与旧的数据
            auditLog.setData(newWriter.toString()).setOldData(oldWriter.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void changedData(
            BeanProperty[] properties, EntityBean entityBean, SpiJsonWriter oldWriter, SpiJsonWriter newWriter)
            throws IOException {
        for (BeanProperty prop : properties) {
            if (prop.isGenerated()) {
                continue;
            }
            if (prop.isImportedPrimaryKey()) {
                continue;
            }
            if (!entityBean._ebean_intercept().isChangedProperty(prop.propertyIndex())) {
                continue;
            }

            var oldValue = entityBean._ebean_intercept().origValue(prop.propertyIndex());
            var newValue = prop.getValue(entityBean);
            if (prop instanceof BeanPropertyAssocMany<?> p) {
                if (oldValue != null) {
                    oldWriter.writeFieldName(prop.name());
                    oldWriter.writeRawValue(p.jsonWriteCollection(oldValue));
                }
                newWriter.writeFieldName(prop.name());
                newWriter.writeRawValue(p.jsonWriteCollection(newValue != null ? newValue : List.of()));
            } else {
                prop.jsonWriteValue(oldWriter, oldValue);
                prop.jsonWriteValue(newWriter, newValue);
            }
        }
    }
}
