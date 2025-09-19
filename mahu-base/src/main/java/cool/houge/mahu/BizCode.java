package cool.houge.mahu;

import java.io.Serializable;

/// 业务错误码。
///
/// 系统错误码采用32位纯数字格式，其中常规错误码为6位数。请注意，以数字“1”开头的6位错误码为系统保留码段，自定义错误码时应避免使用此类编号。
///
/// @author ZY (kzou227@qq.com)
public interface BizCode extends Serializable {

    /// 错误代码
    int code();

    /// 错误描述
    String message();

    /// 对应的 HTTP 状态码
    int httpStatus();
}
