package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/// 获取手机号
///
/// @author ZY (kzou227@qq.com)
@Data
public class GetUserPhoneNumberPayload {

    /// js_code
    private String code;

    // =================================================== //
    /// 客户端 ID
    @JsonIgnore
    private transient String clientId;
}
