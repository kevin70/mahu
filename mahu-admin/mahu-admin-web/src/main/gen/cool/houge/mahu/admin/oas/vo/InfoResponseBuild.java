package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.avaje.validation.constraints.*;

    /**
    * 应用构建信息
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class InfoResponseBuild {

    /**
     * 应用构建时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("time")
    private String time;
    /**
     * Git 提交 Hash 值
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("revision")
    private String revision;
}

