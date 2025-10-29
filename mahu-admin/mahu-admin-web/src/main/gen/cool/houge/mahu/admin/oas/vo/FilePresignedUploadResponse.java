package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class FilePresignedUploadResponse {

    /**
     * 预上传 URL
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("presigned_upload_url")
    private String presignedUploadUrl;
}
