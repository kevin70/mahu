package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.system.Client;
import cool.houge.mahu.entity.system.query.QClient;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

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
    /// | create_time | date-time |
    /// | update_time | date-time |
    /// | client_id | string |
    public PagedList<Client> findPage(DataFilter dataFilter) {
        var qb = new QClient(database);
        var rsqlCtx = RSQLContext.of(qb)
                .property("create_time", qb.createTime)
                .property("update_time", qb.updateTime)
                .property("client_id", qb.clientId);
        super.apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }

    /// 查询支持微信的客户端
    public List<Client> findWechatClient() {
        return new QClient(db())
                .wechatAppid
                .isNotNull()
                .and()
                .wechatAppsecret
                .isNotNull()
                .findList();
    }
}
