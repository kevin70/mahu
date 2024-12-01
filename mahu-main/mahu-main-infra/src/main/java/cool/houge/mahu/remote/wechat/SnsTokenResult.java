package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 通过code换取网页授权access_token.
///
/// @author ZY (kzou227@qq.com)
@Data
public class SnsTokenResult {

    /// 错误信息
    private String errmsg;
    /// 错误码
    ///  - `40029`: invalid code
    private Integer errcode;
    /// 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
    @JsonProperty("access_token")
    private String accessToken;
    /// access_token接口调用凭证超时时间，单位（秒）.
    @JsonProperty("expires_in")
    private Integer expiresIn;
    /// 用户刷新access_token
    @JsonProperty("refresh_token")
    private String refreshToken;
    /// 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
    private String openid;
    /// 用户授权的作用域，使用逗号（`,`）分隔
    private String scope;
    /// 是否为快照页模式虚拟账号，只有当用户是快照页模式虚拟账号时返回，值为1
    @JsonProperty("is_snapshotuser")
    private Integer isSnapshotuser;
    /// 用户统一标识（针对一个微信开放平台账号下的应用，同一用户的 unionid 是唯一的），只有当scope为 `snsapi_userinfo` 时返回
    private String unionid;
}
