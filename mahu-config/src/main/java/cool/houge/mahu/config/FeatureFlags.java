package cool.houge.mahu.config;

/// 功能开关代码常量。
///
/// 用于集中维护 `sys.feature_flags.code` 字段的取值，避免在各处散落硬编码字符串。
/// 建议按业务域分组，例如：
/// - 认证相关开关放在 `Auth` 内部类
/// - 支付相关开关放在 `Payment` 内部类
///
/// 示例（使用时按实际业务补充）：
/// ```java
/// if (featureFlagCacheService.loadFeature(FeatureFlags.Auth.LOGIN_CAPTCHA).isActive()) {
///     // 启用登录验证码
/// }
/// ```
public final class FeatureFlags {

    private FeatureFlags() {
        // utility class
    }

    /// 认证相关功能开关
    public static final class Auth {

        private Auth() {}

        // public static final String LOGIN_CAPTCHA = "auth.login_captcha";
    }

    /// 支付相关功能开关
    public static final class Payment {

        private Payment() {}

        // public static final String WECHAT_PAY = "payment.wechat";
    }
}
