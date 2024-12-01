package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信接口返回结果.
 * @author ZY (kzou227@qq.com)
 */
@Data
public class WechatTokenResult {

    /**
     * <code>0</code> 值代表请求成功.
     *
     * <ul>
     *     <li><code>40029</code> code无效 | js_code无效</li>
     *     <li><code>45011</code> api minute-quota reach limit mustslower retry next minute | API调用太频繁，请稍候再试</li>
     *     <li><code>40226</code> code blocked | 高风险等级用户，小程序登录拦截</li>
     *     <li><code>-1</code> system error | 系统繁忙，此时请开发者稍候再试</li>
     * </ul>
     */
    private int errcode;
    /**
     * 错误描述.
     */
    private String errmsg;
    /**
     * 获取到的凭证.
     */
    @JsonProperty("access_token")
    private String accessToken;
    /**
     * 凭证有效时间（单位：秒）目前是7200秒之内的值.
     */
    @JsonProperty("expires_in")
    private long expiresIn;
    /**
     * 会话密钥.
     */
    @JsonProperty("session_key")
    private String sessionKey;
    /**
     * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回.
     */
    private String unionid;
    /**
     * 用户唯一标识.
     */
    private String openid;
}
