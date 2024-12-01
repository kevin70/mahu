package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

///
///
/// @author ZY (kzou227@qq.com)
@Data
public class Jscode2SessionResult {

    /// 错误信息
    private String errmsg;
    /// 错误码
    ///
    ///  - `40029`: js_code 无效
    ///  - `45011`: API 调用太频繁，请稍候再试
    ///  - `40226`: 高风险等级用户，小程序登录拦截
    ///  - `-1`: 系统繁忙，此时请开发者稍候再试
    ////
    private Integer errcode;
    /// 会话密钥
    @JsonProperty("session_key")
    private String sessionKey;
    /// 用户在开放平台的唯一标识符
    private String unionid;
    /// 用户唯一标识
    private String openid;
}
