package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 微信解密数据结果.
///
/// @author ZY (kzou227@qq.com)
@Data
public class WechatDecryptResult {

    /// 昵称
    @JsonProperty("nickName")
    private String nickName;
    /// 性别
    private Integer gender;
    /// 语言
    private String language;
    /// 省
    private String province;
    /// 城市
    private String city;
    /// 国家代码
    private String country;
    /// 用户头像
    @JsonProperty("avatarUrl")
    private String avatarUrl;
    /// 微信唯一ID
    private String unionId;
    /// 附加信息.
    private Watermark watermark;

    @Data
    public static class Watermark {
        /// 时间戳.
        private Long timestamp;
        /// 应用ID.
        private String appid;
    }
}
