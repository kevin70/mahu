package cool.houge.mahu;

import lombok.experimental.UtilityClass;

/// 枚举工具类
///
/// @author ZY (kzou227@qq.com)
@UtilityClass
public final class EnumUtils {

    /// 将代码转换为枚举
    ///
    /// @param code 代码
    /// @param enumClass 枚举类
    public static <E extends Enum<E> & CodedEnum> E fromCode(int code, Class<E> enumClass) {
        var constants = enumClass.getEnumConstants();
        for (E e : constants) {
            if (e.getCode() == code) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code + " for enum " + enumClass.getName());
    }

    /// 将字符串类型代码转换为枚举
    ///
    /// @param code 代码
    /// @param enumClass 枚举类
    public static <E extends Enum<E> & CodedEnum> E fromCode(String code, Class<E> enumClass) {
        try {
            return fromCode(Integer.parseInt(code), enumClass);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid code: " + code + " for enum " + enumClass.getName());
        }
    }
}
