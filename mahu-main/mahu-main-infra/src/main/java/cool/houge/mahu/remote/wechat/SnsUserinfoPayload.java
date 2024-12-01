package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 拉取用户信息.
///
/// @author ZY (kzou227@qq.com)
@Data
public class SnsUserinfoPayload {

    /// 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同.
    @JsonProperty("access_token")
    private String accessToken;
    /// 用户的唯一标识.
    private String openid;
    /// 返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语.
    private String lang = "zh_CN";
}
