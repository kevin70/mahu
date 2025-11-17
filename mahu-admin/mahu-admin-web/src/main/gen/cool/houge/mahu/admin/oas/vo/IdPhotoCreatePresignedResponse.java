package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class IdPhotoCreatePresignedResponse {

    /**
     * 证件照流水号
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id_photo_id")
    private Long idPhotoId;
    /**
     * 预上传 URL
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("presigned_upload_url")
    private String presignedUploadUrl;
}
