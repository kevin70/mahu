package cool.houge.mahu.system.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.system.Client;
import cool.houge.mahu.entity.system.query.QClient;
import io.ebean.Database;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityNotFoundException;

/// 认证终端
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ClientRepository extends HBeanRepository<String, Client> {

    public ClientRepository(Database db) {
        super(Client.class, db);
    }


    /// 获取客户端配置.
    ///
    /// @param clientId 客户端 ID
    public Client obtainClient(String clientId) {
        return new QClient(database)
            .clientId
            .eq(clientId)
            .findOneOrEmpty()
            .orElseThrow(() -> new EntityNotFoundException("未找到客户端 " + clientId));
    }
}
