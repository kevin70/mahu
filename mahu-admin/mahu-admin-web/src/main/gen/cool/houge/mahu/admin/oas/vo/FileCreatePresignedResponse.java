package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class FileCreatePresignedResponse {

    /**
     * 文件 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("file_id")
    private Long fileId;
    /**
     * 预上传 URL
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("presigned_upload_url")
    private String presignedUploadUrl;
}
