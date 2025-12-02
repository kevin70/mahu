package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class AssetCreatePresignedRequest {

    /**
     * 类型
     * minimum: 0
     * maximum: 10000
     */
     @Min(0) @Max(10000)
    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private Integer type;
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
}
