package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.avaje.validation.constraints.*;

    /**
    * 应用 Java 信息
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class InfoResponseJava {

    /**
     * Java 版本
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("version")
    private String version;
    /**
     * Java 提供商
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("vendor")
    private String vendor;
}
