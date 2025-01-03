package cool.houge.mahu.admin.system.service;

import com.github.f4b6a3.ulid.UlidCreator;
import cool.houge.mahu.admin.shared.SharedToolService;
import cool.houge.mahu.admin.system.repository.ClientRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.Client;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Random;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    @Inject
    SharedToolService toolService;

    /// 新增认证客户端
    @Transactional
    public void save(Client client) {
        var clientId = UlidCreator.getUlid().toString();
        var clientSecret =
                randomClientSecret();
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
    public PagedList<Client> findPage(DataFilter dataFilter) {
        var plist = clientRepository.findPage(dataFilter);
        return toolService.wrap(plist, dataFilter);
    }

    /// 查询指定 ID 的客户端
    @Transactional(readOnly = true)
    public Client findById(String id) {
        return clientRepository.findById(id);
    }

    private String randomClientSecret() {
        var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_+^#*()!";
        var salt = new StringBuilder();
        var rnd = new Random();
        while (salt.length() < 256) { // length of the random string.
            int index = (int) (rnd.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }
        return salt.toString();
    }
}
