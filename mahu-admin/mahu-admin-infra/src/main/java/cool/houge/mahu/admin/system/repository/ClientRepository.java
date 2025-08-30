package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.system.Client;
import cool.houge.mahu.entity.system.query.QClient;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ClientRepository extends HBeanRepository<String, Client> {

    public ClientRepository(Database db) {
        super(Client.class, db);
    }

    /// 获取客户端配置
    /// @param clientId 客户端 ID
    public Client obtainClient(String clientId) {
        return new QClient(database)
                .clientId
                .eq(clientId)
                .findOneOrEmpty()
                .orElseThrow(() -> new EntityNotFoundException("未找到客户端 " + clientId));
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | updated_at | date-time |
    /// | client_id | string |
    public PagedList<Client> findPage(DataFilter dataFilter) {
        return new QClient(db()).also(o -> super.apply(o, dataFilter)).findPagedList();
    }

    @Override
    protected @NonNull List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QClient.Alias.createdAt),
                FilterItem.of(QClient.Alias.updatedAt),
                FilterItem.of(QClient.Alias.clientId)
                //
                );
    }
}
