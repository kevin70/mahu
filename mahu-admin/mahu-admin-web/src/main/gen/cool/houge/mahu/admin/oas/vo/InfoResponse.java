package cool.houge.mahu.admin.oas.vo;

import cool.houge.mahu.admin.oas.vo.InfoResponseBuild;
import cool.houge.mahu.admin.oas.vo.InfoResponseFeaturesInner;
import cool.houge.mahu.admin.oas.vo.InfoResponseJava;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class InfoResponse {

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
     * 应用环境
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("env")
    private String env;
    /**
     * 服务器时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("server_time")
    private String serverTime;
    /**
     * Get build
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("build")
    private InfoResponseBuild build;
    /**
     * Get java
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("java")
    private InfoResponseJava java;
    /**
     * 应用功能
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("features")
    private List<@Valid InfoResponseFeaturesInner> features;
}

