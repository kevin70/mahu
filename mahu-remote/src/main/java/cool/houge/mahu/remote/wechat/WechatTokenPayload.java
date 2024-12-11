package cool.houge.mahu.remote.wechat;

import lombok.Data;

/// 微信请求数据.
///
/// @author ZY (kzou227@qq.com)
@Data
public class WechatTokenPayload {
    /// 微信 appid.
    private String appid;
    /// 微信接口密钥.
    private String secret;
}
