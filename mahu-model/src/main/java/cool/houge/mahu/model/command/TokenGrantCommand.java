package cool.houge.mahu.model.command;

import cool.houge.mahu.config.GrantType;
import lombok.Data;

/// 令牌请求数据
///
/// @author ZY (kzou227@qq.com)
@Data
public class TokenGrantCommand {

    /// 客户端请求IP
    private String clientIp;
    /// 授权类型
    private GrantType grantType;
    /// 客户端 ID
    private String clientId;
    /// 用户名
    private String username;
    /// 登录密码
    private String password;
    /// 刷新令牌
    private String refreshToken;

    /// 微信应用 ID
    private String wechatAppid;
    /// 微信 JS_CODE
    private String wechatJsCode;
    /// 微信加密数据
    private String wechatEncryptData;
    /// 微信加密
    private String wechatIv;
    /// 微信网页授权码
    private String wechatCode;
}
