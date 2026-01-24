package cool.houge.mahu.admin.security;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.common.context.Contexts;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// 认证用户的信息
///
/// @author ZY (kzou227@qq.com)
public interface AuthContext {

    /// 匿名访问
    AuthContext ANONYMOUS = new AuthContext() {
        @Override
        public int adminId() {
            throw new UnsupportedOperationException("anonymous");
        }

        @Override
        public String name() {
            throw new UnsupportedOperationException("anonymous");
        }

        @Override
        public boolean hasPermission(String code) {
            throw new UnsupportedOperationException("anonymous");
        }

        @Override
        public List<String> permissions() {
            throw new UnsupportedOperationException("anonymous");
        }
    };

    /// 返回认证的管理员 ID
    int adminId();

    /// 返回认证用户的名称
    String name();

    /// 检查用户是否拥有权限
    ///
    /// @param code 需要校验的权限对象
    boolean hasPermission(String code);

    /// 返回用户拥有的权限代码
    List<String> permissions();

    /// 获取当前线程上下文中的认证用户信息
    static @NonNull AuthContext current() {
        return Contexts.context()
                .orElseThrow(() -> new BizCodeException(BizCodes.DATA_LOSS, "没有发现 helidon 上下文对象"))
                .get(AuthContext.class)
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAUTHENTICATED, "缺少认证上下文"));
    }
}
