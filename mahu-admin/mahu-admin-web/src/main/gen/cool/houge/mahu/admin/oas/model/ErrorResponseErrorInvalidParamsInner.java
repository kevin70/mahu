package cool.houge.mahu.admin.oas.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class ErrorResponseErrorInvalidParamsInner {

    /**
     * 参数名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("property_name")
    private String propertyName;
    /**
     * 错误描述
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("message")
    private String message;
}

