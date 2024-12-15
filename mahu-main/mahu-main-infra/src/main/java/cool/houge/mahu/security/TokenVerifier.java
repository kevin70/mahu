package cool.houge.mahu.security;

/// 访问令牌校验
///
/// @author ZY (kzou227@qq.com)
public interface TokenVerifier {

    /// 验证访问令牌
    ///
    /// @param token 访问令牌
    AuthContext verify(String token);
}
