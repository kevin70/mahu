package cool.houge.mahu.admin.sys.service;

import com.github.f4b6a3.ulid.UlidCreator;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.query.AuthClientQuery;
import cool.houge.mahu.repository.sys.AuthClientRepository;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class AuthClientService {

    private final EntityBeanMapper beanMapper;
    private final AuthClientRepository authClientRepository;

    /// 新增认证客户端
    @Transactional
    public void save(AuthClient authClient) {
        var clientId = UlidCreator.getUlid().toString();
        var clientSecret = randomClientSecret();
        authClient.setDeleted(false).setClientId(clientId).setClientSecret(clientSecret);
        authClientRepository.save(authClient);
    }

    /// 更新认证客户端
    @Transactional
    public void update(AuthClient authClient) {
        var dbEntity = this.findById(authClient.getClientId());
        beanMapper.map(dbEntity, authClient);
        authClientRepository.update(dbEntity);
    }

    /// 删除认证客户端
    @Transactional
    public void delete(AuthClient authClient) {
        authClientRepository.delete(authClient);
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<AuthClient> findPage(AuthClientQuery query, Page page) {
        return authClientRepository.findPage(query, page);
    }

    /// 查询指定 ID 的客户端
    @Transactional(readOnly = true)
    public AuthClient findById(String id) {
        var bean = authClientRepository.findById(id);
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
