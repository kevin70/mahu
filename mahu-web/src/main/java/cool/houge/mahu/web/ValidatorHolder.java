package cool.houge.mahu.web;

import io.avaje.validation.Validator;

/// 数据校器验持有对象
///
/// @author ZY (kzou227@qq.com)
class ValidatorHolder {

    /// 数据校验器
    static final Validator VALIDATOR = Validator.builder().build();
}
