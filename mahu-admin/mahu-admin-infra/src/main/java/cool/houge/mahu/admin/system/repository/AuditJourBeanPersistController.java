package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.entity.system.*;
import cool.houge.mahu.common.Metadata;
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

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

/// 审计日志
///
/// @author ZY (kzou227@qq.com)
public class AuditJourBeanPersistController extends BeanPersistAdapter {

    @Override
    public boolean isRegisterFor(Class<?> cls) {
        return cls == Dict.class
                || cls == Client.class
                || cls == Role.class
                || cls == Department.class
                || cls == Employee.class;
    }

    @Override
    public void postInsert(BeanPersistRequest<?> request) {
        saveAuditJour(ChangeType.INSERT, request);
    }

    @Override
    public void postUpdate(BeanPersistRequest<?> request) {
        saveAuditJour(ChangeType.UPDATE, request);
    }

    @Override
    public void postDelete(BeanPersistRequest<?> request) {
        saveAuditJour(ChangeType.DELETE, request);
    }

    @Override
    public void postSoftDelete(BeanPersistRequest<?> request) {
        saveAuditJour(ChangeType.UPDATE, request);
    }

    void saveAuditJour(ChangeType changeType, BeanPersistRequest<?> request) {
        var server = (DefaultServer) request.database();
        var bean = (EntityBean) request.bean();
        var descriptor = server.descriptor(bean.getClass());

        var entity = new AuditJour()
                .setSource(server.config().getDataSourceConfig().getApplicationName())
                .setTableName(descriptor.baseTable())
                .setChangeType(changeType.getCode())
                .setData(changeData(server, descriptor, bean));

        var id = descriptor.getId(bean);
        if (id != null) {
            entity.setDataId(descriptor.idProperty().format(id));
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
