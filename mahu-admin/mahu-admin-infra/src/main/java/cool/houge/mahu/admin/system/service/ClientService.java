package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.admin.system.repository.ClientRepository;
import cool.houge.mahu.entity.AuthClient;
import cool.houge.mahu.domain.DataFilter;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import io.hypersistence.tsid.TSID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class ClientService {

    private final EntityBeanMapper beanMapper;
    private final ClientRepository clientRepository;

    /// 新增认证客户端
    @Transactional
    public void save(AuthClient authClient) {
        var clientId = TSID.fast().toString();
        var clientSecret = randomClientSecret();
        authClient.setDeleted(false).setClientId(clientId).setClientSecret(clientSecret);
        clientRepository.save(authClient);
    }

    /// 更新认证客户端
    @Transactional
    public void update(AuthClient authClient) {
        var dbEntity = this.findById(authClient.getClientId());
        beanMapper.map(dbEntity, authClient);
        clientRepository.update(dbEntity);
    }

    /// 删除认证客户端
    @Transactional
    public void delete(AuthClient authClient) {
        clientRepository.delete(authClient);
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<AuthClient> findPage(DataFilter dataFilter) {
        return clientRepository.findPage(dataFilter);
    }

    /// 查询指定 ID 的客户端
    @Transactional(readOnly = true)
    public AuthClient findById(String id) {
        var bean = clientRepository.findById(id);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到认证客户端[" + id + "]");
        }
        return bean;
    }

    private String randomClientSecret() {
        var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_+^#*()!";
        var salt = new StringBuilder();
        var rnd = ThreadLocalRandom.current();
        while (salt.length() < 256) { // length of the random string.
            int index = rnd.nextInt(chars.length());
            salt.append(chars.charAt(index));
        }
        return salt.toString();
    }
}
