package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 获取手机号
///
/// @author ZY (kzou227@qq.com)
@Data
public class GetUserPhoneNumberResult {

    private int errcode;
    private String errmsg;

    @JsonProperty("phone_info")
    private PhoneInfo phoneInfo;

    @Data
    public static class PhoneInfo {
        // ===================== 手机号码 ===================== //
        /// 用户绑定的手机号（国外手机号会有区号）.
        private String phoneNumber;
        /// 没有区号的手机号
        private String purePhoneNumber;
        /// 区号
        private String countryCode;

        private Watermark watermark;
    }

    @Data
    public static class Watermark {
        /// 时间戳
        private Long timestamp;
        /// 应用ID
        private String appid;
    }
}
