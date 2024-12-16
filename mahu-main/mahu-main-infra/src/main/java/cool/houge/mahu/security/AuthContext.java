package cool.houge.mahu.security;

import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import io.helidon.common.context.Contexts;

import javax.annotation.Nonnull;
import java.util.List;

/// 认证用户的信息
///
/// @author ZY (kzou227@qq.com)
public interface AuthContext {

    /// 返回认证的用户ID
    long uid();

    /// 返回认证用户的名称
    String name();

    /// 检查用户是否拥有权限
    ///
    /// @param obj 需要校验的权限对象
    default boolean checkPermit(Object obj) {
        return false;
    }

    /// 返回用户拥有的权限代码
    default List<String> permits() {
        return List.of();
    }

    /// 返回用户商店 IDs
    default List<Integer> shopIds() {
        return List.of();
    }

    /// 获取当前线程上下文中的认证用户信息
    static @Nonnull AuthContext get() {
        return Contexts.context()
                .orElseThrow(() -> new BizCodeException(BizCodes.DATA_LOSS, "没有发现 helidon 上下文对象"))
                .get(AuthContext.class)
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAUTHENTICATED, "缺少认证上下文"));
    }
}
