package cool.houge.mahu.config;

import lombok.Getter;

/// 授权类型定义
///
/// @author ZY (kzou227@qq.com)
@Getter
public enum GrantType {
    /// 用户名密码登录
    PASSWORD("password"),
    /// 刷新令牌
    REFRESH_TOKEN("refresh_token"),
    /// [微信小程序登录](https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html)
    WECHAT_XCX("wechat_xcx"),
    /// [微信网页授权](https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html)
    WECHAT_WEB("wechat_web"),
    ;

    public final String code;

    GrantType(String code) {
        this.code = code;
    }

    public boolean matches(String code) {
        return this.code.equals(code);
    }
}
