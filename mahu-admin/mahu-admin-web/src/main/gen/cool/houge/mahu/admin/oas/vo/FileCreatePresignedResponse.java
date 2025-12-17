package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class FileCreatePresignedResponse {

    /**
     * 对象 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("object_id")
    private Long objectId;
    /**
     * 上传 URL
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("upload_url")
    private String uploadUrl;
    /**
     * 上传成功后的访问 URL
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("access_url")
    private String accessUrl;
}
