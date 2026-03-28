package cool.houge.mahu.config;

/// 终端类型枚举
///
/// 定义了应用支持的不同客户端/终端类型，用于区分请求来源和应用登录方式。
///
/// @author ZY (kzou227@qq.com)
public enum TerminalType {
    /// 微信小程序登录
    WECHAT_XCX,
    /// 微信网页授权登录
    WECHAT_WEB,
    /// Web 浏览器访问
    BROWSER,
}
