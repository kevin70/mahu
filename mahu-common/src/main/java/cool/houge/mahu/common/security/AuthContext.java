package cool.houge.mahu.common.security;

import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;

import java.util.List;

/// 认证用户上下文
///
/// @author ZY (kzou227@qq.com)
public interface AuthContext {

    /// 返回此用户的ID
    long uid();

    /// 返回此用户的名称
    String name();

    /// 检查许可证，返回 `false` 表示校验不通过
    ///
    /// @param permits 权限代码
    boolean containsPermits(String... permits);

    /// 验证权限许可证
    ///
    /// @param permits 权限代码
    /// @see BizCodeException
    /// @see BizCodes#PERMISSION_DENIED 没有权限抛出的错误码
    default void validatePermit(String... permits) throws BizCodeException {
        if (!containsPermits(permits)) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED);
        }
    }

    /// 用户拥有的权限代码
    default List<String> rolePermits() {
        return List.of();
    }
}
