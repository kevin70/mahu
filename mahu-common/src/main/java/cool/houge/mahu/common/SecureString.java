package cool.houge.mahu.common;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

/// 安全的字符串
///
/// @author ZY (kzou227@qq.com)
@Getter
public final class SecureString {

    @JsonUnwrapped
    private final String value;

    private SecureString(String value) {
        this.value = value;
    }

    /// 创建一个字符串密钥
    @JsonCreator
    public static SecureString of(String value) {
        requireNonNull(value);
        return new SecureString(value);
    }

    @Override
    public String toString() {
        return "******";
    }
}
