package cool.houge.mahu;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/// 应用环境枚举类
///
/// 定义软件开发生命周期中各阶段的环境，提供以下核心功能：
/// - 环境类型判断（开发/测试/生产级环境）
/// - 字符串与枚举的双向转换
/// - 多来源配置的当前环境自动识别
///
/// 支持的环境配置来源（按优先级排序）：
/// 1. `MAHU_ENV`
/// 2. `mahu.env`
/// 3. `HELIDON_CONFIG_PROFILE`
/// 4. `helidon.config.profile`
/// 5. 默认值（开发环境）
///
/// @author ZY (kzou227@qq.com)
public enum Env {

    /// 开发环境
    ///
    /// 适用场景：
    /// - 开发者本地编码调试
    /// - 单元测试与集成测试
    /// - 开发团队共享开发服务器
    ///
    /// 特性：
    /// - 启用详细日志输出
    /// - 连接开发数据库
    /// - 开启热部署支持
    DEV("dev", "Development Environment"),

    /// 系统集成测试环境
    ///
    /// 适用场景：
    /// - 多模块/服务间交互验证
    /// - 接口契约测试
    /// - 持续集成流水线自动测试
    SIT("sit", "System Integration Testing"),

    /// 用户验收测试环境
    ///
    /// 适用场景：
    /// - 业务方功能验证
    /// - 需求规格符合性测试
    /// - 性能与安全测试
    ///
    /// 数据特点：
    /// - 使用脱敏的模拟生产数据
    /// - 数据量接近真实场景
    UAT("uat", "User Acceptance Testing"),

    /// 预发布环境
    ///
    /// 适用场景：
    /// - 生产发布前最终验证
    /// - 配置与生产环境一致性检查
    /// - 灰度发布演练
    ///
    /// 特性：
    /// - 配置与生产环境完全一致
    /// - 数据为生产数据的子集
    /// - 不对外提供服务
    STG("stg", "Staging Environment"),

    /// 生产环境
    ///
    /// 适用场景：
    /// - 面向最终用户的线上服务
    /// - 正式业务数据处理
    ///
    /// 特性：
    /// - 最高安全级别配置
    /// - 性能优化优先
    /// - 最小化日志输出
    /// - 高可用性保障
    PROD("prod", "Production Environment");

    /// 环境简写名称（如`dev`、`prod`）
    private final String shortName;
    /// 环境完整名称（如 `Development Environment`）
    private final String fullName;
    private static final Map<String, Env> BY_SHORT_NAME = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(env -> env.shortName, Function.identity()));

    /// 构造环境枚举实例
    ///
    /// @param shortName 环境简写，用于配置文件和系统变量
    /// @param fullName 环境全称，用于日志和用户展示
    Env(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    /// 获取环境简写名称
    ///
    /// @return 环境简写字符串（非空）
    public String getShortName() {
        return shortName;
    }

    /// 获取环境完整名称
    ///
    /// @return 环境全称字符串（非空）
    public String getFullName() {
        return fullName;
    }

    /// 判断当前环境是否为开发环境
    ///
    /// ```java
    /// if (Env.current().isDev()) {
    ///     // 开发环境专属逻辑
    /// }
    /// ```
    ///
    /// @return 若为`DEV环境则返回`true`，否则返回`false`
    public boolean isDev() {
        return this == DEV;
    }

    /// 判断当前环境是否为测试环境
    ///
    /// 测试环境包括：
    /// - `SIT`（系统集成测试）
    /// - `UAT`（用户验收测试）
    ///
    /// @return 若为测试环境则返回`true`，否则返回`false`
    public boolean isTest() {
        return this == SIT || this == UAT;
    }

    /// 判断当前环境是否为生产级环境
    ///
    /// 生产级环境包括：
    /// - `STG`（预发布）
    /// - `PROD`（生产）
    ///
    /// 通常用于执行需要严格权限或性能优化的操作。
    ///
    /// @return 若为生产级环境则返回`true`，否则返回`false`
    public boolean isProd() {
        return this == STG || this == PROD;
    }

    /// 环境配置属性键名。
    public static final String ENV_PROPERTY = "mahu.env";
    /// 环境变量形式的环境配置键名。
    public static final String ENV_VARIABLE = "MAHU_ENV";
    /// Helidon profile 属性键名。
    public static final String HELIDON_PROFILE_PROPERTY = "helidon.config.profile";
    /// Helidon profile 环境变量键名。
    public static final String HELIDON_PROFILE_ENV_VARIABLE = "HELIDON_CONFIG_PROFILE";
    /// 将字符串转换为环境枚举
    ///
    /// 支持不区分大小写的匹配，例如：
    /// - `Dev` → [#DEV]
    /// - `PROD` → [#PROD]
    ///
    /// @param env 环境字符串（不可为`null`或空白）
    /// @return 对应的 Env 枚举实例
    /// @throws IllegalArgumentException 若输入无法匹配任何环境
    public static Env of(String env) {
        var normalizedEnv = normalize(env);
        if (normalizedEnv == null) {
            throw new IllegalArgumentException("env cannot be null or empty");
        }
        var resolved = BY_SHORT_NAME.get(normalizedEnv);
        if (resolved != null) {
            return resolved;
        }
        throw new IllegalArgumentException("unknown env: " + env);
    }

    /// 获取当前运行环境
    ///
    /// 环境识别流程：
    /// 1. `MAHU_ENV`
    /// 2. `mahu.env`
    /// 3. `HELIDON_CONFIG_PROFILE`
    /// 4. `helidon.config.profile`
    /// 5. 默认 `DEV`
    ///
    /// @return 当前应用运行的环境枚举实例（非 null）
    public static Env current() {
        return CurrentEnvHolder.CURRENT;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", fullName, shortName);
    }

    /// 从多来源解析当前环境。
    static Env resolveCurrent(String... candidates) {
        for (String candidate : candidates) {
            var normalized = normalize(candidate);
            if (normalized != null) {
                return of(normalized);
            }
        }
        return Env.DEV;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static final class CurrentEnvHolder {
        private static final Env CURRENT = resolveCurrent(
                System.getenv(ENV_VARIABLE),
                System.getProperty(ENV_PROPERTY),
                System.getenv(HELIDON_PROFILE_ENV_VARIABLE),
                System.getProperty(HELIDON_PROFILE_PROPERTY));

        private CurrentEnvHolder() {}
    }
}
