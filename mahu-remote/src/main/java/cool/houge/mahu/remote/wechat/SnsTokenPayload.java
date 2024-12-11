package cool.houge.mahu.remote.wechat;

import lombok.Data;

/// 通过 code 换取网页授权 access_token 请求参数
///
/// [微信文档](https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html)
/// @author ZY (kzou227@qq.com)
@Data
public class SnsTokenPayload {

    private String appid;
    private String secret;
    private String code;
}
