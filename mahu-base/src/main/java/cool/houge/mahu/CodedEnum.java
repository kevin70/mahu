package cool.houge.mahu;

import java.util.Objects;

/// 数值枚举
///
/// @author ZY (kzou227@qq.com)
public interface CodedEnum {

    /// 枚举代码
    int getCode();

    /// 判断当前枚举代码是否与指定代码相等
    default boolean eq(Integer code) {
        return Objects.equals(getCode(), code);
    }

    /// 判断当前枚举代码是否与指定代码不相等
    default boolean neq(Integer code) {
        return !eq(code);
    }
}
