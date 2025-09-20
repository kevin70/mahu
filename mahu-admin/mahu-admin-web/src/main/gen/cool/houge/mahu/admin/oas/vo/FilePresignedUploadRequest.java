package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class FilePresignedUploadRequest {

    /**
     * 上传文件的名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("file_name")
    private String fileName;
    /**
     * 文件大小
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("file_size")
    private Long fileSize;
    /**
     * 文件类型
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("mime_type")
    private String mimeType;

                /**
    * 类型
    */
    public enum KindEnum {
        ADMIN_AVATAR("ADMIN_AVATAR");

        private String value;

        KindEnum(String value) {
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
        public static KindEnum fromValue(String text) {
            for (KindEnum b : KindEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + text + "'");
        }
    }


    /**
     * 类型
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("kind")
    private KindEnum kind;
}

