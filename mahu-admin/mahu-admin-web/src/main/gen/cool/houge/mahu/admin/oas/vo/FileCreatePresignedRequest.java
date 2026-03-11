package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cool.houge.mahu.admin.oas.vo.FileType;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class FileCreatePresignedRequest {

    /**
     * Get type
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private FileType type;
    /**
     * 上传文件的名称
     */
      @NotNull
 @Size(min=1,max=128)
    @com.fasterxml.jackson.annotation.JsonProperty("file_name")
    private String fileName;
    /**
     * 文件大小
     * minimum: 1
     */
     @Min(1L)
    @com.fasterxml.jackson.annotation.JsonProperty("file_size")
    private Long fileSize;
}
