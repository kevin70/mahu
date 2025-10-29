package cool.houge.mahu.admin.oas.vo;

import cool.houge.mahu.admin.oas.vo.ErrorResponseError;
import io.avaje.validation.constraints.*;

    /**
    * 错误响应
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class ErrorResponse {

    /**
     * Get error
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("error")
    private ErrorResponseError error;
}
