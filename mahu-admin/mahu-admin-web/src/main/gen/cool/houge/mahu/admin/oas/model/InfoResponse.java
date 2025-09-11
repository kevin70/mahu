package cool.houge.mahu.admin.oas.model;

import cool.houge.mahu.admin.oas.model.InfoResponseStatus;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class InfoResponse {

    /**
     * 应用环境
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("env")
    private String env;
    /**
     * 应用名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 应用版本
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("version")
    private String version;
    /**
     * 修订版本，Git 提交 Hash 值
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("revision")
    private String revision;
    /**
     * 应用构建时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("build_time")
    private String buildTime;
    /**
     * Java 版本
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("java_version")
    private String javaVersion;
    /**
     * Java 提供商
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("java_vendor")
    private String javaVendor;
    /**
     * Helidon 版本
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("helidon_version")
    private String helidonVersion;
    /**
     * Get status
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private InfoResponseStatus status;
}

