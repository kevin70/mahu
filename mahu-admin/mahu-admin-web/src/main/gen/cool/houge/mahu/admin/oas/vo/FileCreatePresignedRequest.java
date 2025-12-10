package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class FileCreatePresignedRequest {

    /**
     * 类型
     * minimum: 0
     */
      @NotNull
 @Min(0)
    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private Integer type;
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
