package cool.houge.mahu.config;

/// 配置项键/配置前缀
///
/// @author ZY (kzou227@qq.com)
public interface ConfigKeys {

    /// 服务配置
    String SERVER = "server";

    ///  跨域配置
    String CORS = "cors";

    /// 应用信息配置前缀
    String INFO = "info";

    /// 数据库配置
    String DB = "db";

    /// 访问令牌配置前缀
    String TOKEN = "token";

    /// JWT密钥配置
    String JWT_KEYS = "jwt.keys";

    /// 对象存储配置前缀
    String OSS = "oss";
}
