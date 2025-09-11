package cool.houge.mahu.admin.oas.model;

import io.avaje.validation.constraints.*;

    /**
    * 商店资源预上传
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class GetPresignedUploadShopAssetForm {

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
    /**
     * 商店 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("shop_id")
    private Integer shopId;
}

