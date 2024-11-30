package cool.houge.mahu.admin.system.dto;

import lombok.Data;

/// 令牌返回结果
///
/// @author ZY (kzou227@qq.com)
@Data
public class TokenResult {

    /// 访问令牌
    private String accessToken;
    /// 过期时间
    private long expiresIn;
    /// 刷新令牌
    private String refreshToken;
}
