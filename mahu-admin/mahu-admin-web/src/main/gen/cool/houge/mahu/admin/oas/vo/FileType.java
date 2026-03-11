package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 上传文件类型
 */

public enum FileType {

    ADMIN_AVATAR("ADMIN_AVATAR");

    private String value;

    FileType(String value) {
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
    public static FileType fromValue(String text) {
        for (FileType b : FileType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
}
