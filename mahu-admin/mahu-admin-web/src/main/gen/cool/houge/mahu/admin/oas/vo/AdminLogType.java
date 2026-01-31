package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 管理员日志类型
 */
public enum AdminLogType {
    ACCESS("ACCESS"),
    AUTH("AUTH"),
    CHANGE("CHANGE");

    private final String value;

    AdminLogType(String value) {
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
    public static AdminLogType fromValue(String text) {
        for (AdminLogType b : AdminLogType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
}
