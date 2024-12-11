package cool.houge.mahu.remote.wechat;

import lombok.Data;

/// 请求参数.
///
/// @author ZY (kzou227@qq.com)
@Data
public class Jscode2SessionPayload {

    /// 小程序 appId
    private String appid;
    /// 小程序 appSecret
    private String secret;
    /// 登录时获取的 code
    private String jsCode;
}
