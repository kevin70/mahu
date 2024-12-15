package cool.houge.mahu.common;

import java.util.Optional;

/// 业务错误码
///
/// @author ZY (kzou227@qq.com)
public interface BizCode {

    /// 错误代码
    int code();

    /// 错误描述
    String message();

    /// 子业务错误码.
    Optional<String> subcode();
}
