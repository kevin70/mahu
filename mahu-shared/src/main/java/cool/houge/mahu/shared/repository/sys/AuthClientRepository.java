package cool.houge.mahu.shared.repository.sys;

import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.entity.sys.query.QAuthClient;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class AuthClientRepository extends HBeanRepository<String, AuthClient> {

    public AuthClientRepository(Database db) {
        super(AuthClient.class, db);
    }

    /// 获取客户端配置
    /// @param clientId 客户端 ID
    public AuthClient obtainClient(String clientId) {
        return new QAuthClient(db())
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
    public PagedList<AuthClient> findPage(DataFilter dataFilter) {
        return new QAuthClient(db())
                .also(o -> super.apply(o.query(), dataFilter, filterableItems()))
                .findPagedList();
    }

    List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QAuthClient.Alias.createdAt),
                FilterItem.of(QAuthClient.Alias.updatedAt),
                FilterItem.of(QAuthClient.Alias.clientId)
                //
                );
    }
}
