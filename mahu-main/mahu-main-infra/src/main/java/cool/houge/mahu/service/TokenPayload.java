package cool.houge.mahu.service;

import cool.houge.mahu.common.GrantType;
import lombok.Data;

/// 访问令牌
///
/// @author ZY (kzou227@qq.com)
@Data
public class TokenPayload {

    /// 权限类型
    private GrantType grantType;
    /// 客户端 ID
    private String clientId;

    /// 刷新令牌
    private String refreshToken;

    /// 登录名
    private String username;
    /// 登录密码
    private String password;

    /// 小程序登录验证码
    private String wechatJsCode;
    /// 微信加密数据
    private String wechatEncryptData;
    /// 微信加密
    private String wechatIv;
}
