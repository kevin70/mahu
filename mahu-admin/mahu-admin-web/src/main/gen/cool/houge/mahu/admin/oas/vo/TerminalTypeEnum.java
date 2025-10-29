package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 终端类型
 */

public enum TerminalTypeEnum {

    WECHAT_XCX("WECHAT_XCX"),
    WECHAT_WEB("WECHAT_WEB"),
    BROWSER("BROWSER");

    private String value;

    TerminalTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static TerminalTypeEnum fromValue(String text) {
        for (TerminalTypeEnum b : TerminalTypeEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
}
