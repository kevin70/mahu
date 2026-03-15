package cool.houge.mahu.repository.sys;

import com.google.common.base.Strings;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.entity.sys.query.QAuthClient;
import cool.houge.mahu.query.AuthClientQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import jakarta.persistence.EntityNotFoundException;

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
    public PagedList<AuthClient> findPage(AuthClientQuery query, Page page) {
        var qb = new QAuthClient(db());
        if (!Strings.isNullOrEmpty(query.getClientId())) {
            qb.clientId.eq(query.getClientId());
        }
        if (query.getTerminalType() != null) {
            qb.terminalType.eq(query.getTerminalType());
        }
        if (!Strings.isNullOrEmpty(query.getWechatAppid())) {
            qb.wechatAppid.eq(query.getWechatAppid());
        }

        return super.findPage(qb, page);
    }
}
