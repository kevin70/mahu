package cool.houge.mahu.config;

/// 配置前缀常量定义
///
/// 集中管理应用中所有配置的前缀常量，便于配置读取和维护。
/// 这些前缀用于 `application.yaml` 或环境变量中的配置值查询。
///
/// @author ZY (kzou227@qq.com)
public class ConfigPrefixes {

    private ConfigPrefixes() {
        // utility class
    }

    /// Web 服务器配置前缀，对应 `application.yaml` 中的 `server.*`
    public static final String SERVER = "server";

    /// CORS 跨域配置前缀，对应 `application.yaml` 中的 `cors.*`
    public static final String CORS = "cors";

    /// 应用信息配置前缀，对应 `application.yaml` 中的 `info.*`
    public static final String INFO = "info";

    /// 数据库配置前缀，对应 `application.yaml` 中的 `db.*`
    public static final String DB = "db";

    /// 令牌（Token）配置前缀，对应 `application.yaml` 中的 `token.*`
    public static final String TOKEN = "token";

    /// JWT 密钥配置前缀，对应 `application.yaml` 中的 `jwt.keys.*`
    public static final String JWT_KEYS = "jwt.keys";

    /// 对象存储（OSS）配置前缀，对应 `application.yaml` 中的 `oss.*`
    public static final String OSS = "oss";
}
