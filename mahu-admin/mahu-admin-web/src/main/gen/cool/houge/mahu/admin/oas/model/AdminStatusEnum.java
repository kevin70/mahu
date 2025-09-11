package cool.houge.mahu.admin.oas.model;

import io.avaje.validation.constraints.*;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 管理员状态
 */

public enum AdminStatusEnum {

    ACTIVE("ACTIVE"),
    DISABLED("DISABLED");

    private String value;

    AdminStatusEnum(String value) {
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
    public static AdminStatusEnum fromValue(String text) {
        for (AdminStatusEnum b : AdminStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
}

