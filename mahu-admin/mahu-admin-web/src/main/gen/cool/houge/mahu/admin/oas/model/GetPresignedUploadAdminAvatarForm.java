package cool.houge.mahu.admin.oas.model;

import io.avaje.validation.constraints.*;

    /**
    * 管理员头像预上传
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class GetPresignedUploadAdminAvatarForm {

    /**
     * 类型
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("kind")
    private String kind;
    /**
     * 上传文件的名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("file_name")
    private String fileName;
}

