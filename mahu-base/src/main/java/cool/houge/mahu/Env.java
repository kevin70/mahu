package cool.houge.mahu;

/// 应用环境定义
///
/// @author ZY (kzou227@qq.com)
public enum Env {

    /// 开发环境，开发者本地或共享编码调试环境
    DEV("dev", "Development Environment"),

    /// 系统集成测试环境，验证多个模块/服务间的交互
    SIT("sit", "System Integration Testing"),

    /// 用户验收测试环境，业务方验证功能是否符合需求
    UAT("uat", "User Acceptance Testing"),

    /// 预发布环境，功能与[#PROD]一致，用于最终验证
    STG("stg", "Staging Environment"),

    /// 生产环境，面向真实用户的线上环境
    PROD("prod", "Production Environment");

    private final String shotName;
    private final String fullName;

    Env(String shotName, String fullName) {
        this.shotName = shotName;
        this.fullName = fullName;
    }

    /// 返回应用环境简写
    public String getShotName() {
        return shotName;
    }

    /// 返回应用环境全称
    public String getFullName() {
        return fullName;
    }

    /// 是否为开发环境
    public boolean isDev() {
        return this == DEV;
    }

    /// 是否为测试环境
    public boolean isTest() {
        return this == SIT || this == UAT;
    }

    /// 是否为生产环境
    public boolean isProd() {
        return this == STG || this == PROD;
    }

    /// 将字符串转换为枚举
    public static Env of(String env) {
        if ("dev".equalsIgnoreCase(env)) {
            return DEV;
        }
        if ("sit".equalsIgnoreCase(env)) {
            return SIT;
        }
        if ("uat".equalsIgnoreCase(env)) {
            return UAT;
        }
        if ("stg".equalsIgnoreCase(env)) {
            return STG;
        }
        if ("prod".equalsIgnoreCase(env)) {
            return PROD;
        }
        throw new IllegalArgumentException("unknown env: " + env);
    }
}
