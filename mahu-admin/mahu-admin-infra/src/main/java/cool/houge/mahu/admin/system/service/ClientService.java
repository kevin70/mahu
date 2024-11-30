package cool.houge.mahu.admin.system.service;

import com.github.f4b6a3.ulid.UlidCreator;
import cool.houge.util.NanoIdUtils;
import cool.houge.mahu.admin.system.repository.ClientRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.Client;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    /// 新增认证客户端
    @Transactional
    public void save(Client client) {
        var clientId = UlidCreator.getUlid().toString();
        var clientSecret =
                NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 256);
        client.setDeleted(false).setClientId(clientId).setClientSecret(clientSecret);
        clientRepository.save(client);
    }

    /// 更新认证客户端
    @Transactional
    public void update(Client client) {
        clientRepository.update(client);
    }

    /// 删除认证客户端
    @Transactional
    public void delete(Client client) {
        clientRepository.delete(client);
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Client> findPage(DataFilter filter) {
        return clientRepository.findPage(filter);
    }

    /// 查询指定 ID 的客户端
    @Transactional(readOnly = true)
    public Client findById(String id) {
        return clientRepository.findById(id);
    }
}
