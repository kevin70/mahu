package cool.houge.mahu.admin.oas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import cool.houge.mahu.admin.oas.model.GetPresignedUploadAdminAvatarForm;
import cool.houge.mahu.admin.oas.model.GetPresignedUploadShopAssetForm;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class GetPresignedUploadRequest {

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

