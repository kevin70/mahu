package cool.houge.mahu.admin.security;

/// 访问令牌校验器。
///
/// - 验证 JWT 访问令牌是否有效（签名、时效等）
/// - 成功时解析出认证上下文（`AuthContext`）
/// - 失败时抛出上层可识别的异常（如 JwtException、BizCodeException）
///
/// @author ZY (kzou227@qq.com)
public interface TokenVerifier {

    /// 校验访问令牌并返回认证上下文。
    ///
    /// @param token Bearer token 字符串（不含 "Bearer " 前缀）
    AuthContext verify(String token);
}
