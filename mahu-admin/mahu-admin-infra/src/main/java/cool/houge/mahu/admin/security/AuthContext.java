package cool.houge.mahu.admin.security;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.common.context.Contexts;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// 认证用户上下文。
///
/// - 表示当前请求对应的后台管理员（ID、名称、权限）
/// - 提供匿名上下文常量 ANONYMOUS
/// - 提供从 Helidon 上下文中获取当前认证信息的静态方法
///
/// @author ZY (kzou227@qq.com)
public interface AuthContext {

    /// 匿名访问上下文。
    ///
    /// - adminId 返回 -1
    /// - name 返回 "ANONYMOUS"
    /// - hasPermission 始终返回 false
    /// - permissions 返回空列表
    AuthContext ANONYMOUS = new AuthContext() {
        @Override
        public int adminId() {
            return -1;
        }

        @Override
        public String name() {
            return "ANONYMOUS";
        }

        @Override
        public boolean hasPermission(String code) {
            return false;
        }

        @Override
        public List<String> permissions() {
            return List.of();
        }
    };

    /// 返回认证的管理员 ID（后台 admin 主键）
    int adminId();

    /// 返回认证用户的名称
    String name();

    /// 检查用户是否拥有指定权限编码（参见 Permissions 枚举）
    ///
    /// @param code 需要校验的权限对象
    boolean hasPermission(String code);

    /// 返回用户拥有的权限编码列表
    List<String> permissions();

    /// 从当前线程的 Helidon 上下文中获取认证用户信息。
    ///
    /// 若不存在上下文或认证信息，将抛出 BizCodeException：
    /// - DATA_LOSS：未找到 Helidon 上下文
    /// - UNAUTHENTICATED：缺少认证上下文（可能是 Filter 未正确注册）
    static @NonNull AuthContext current() {
        return Contexts.context()
                .orElseThrow(() -> new BizCodeException(BizCodes.DATA_LOSS, "没有发现 Helidon 上下文对象"))
                .get(AuthContext.class)
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAUTHENTICATED, "缺少认证上下文"));
    }
}
