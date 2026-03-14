package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HFeatureService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/sys/feature-flags", authenticate().andAuthorize("SYS_FEATURE_FLAG:W"), this::createSysFeatureFlag);
        rules.get("/sys/features/{feature_id}", authenticate().andAuthorize("SYS_FEATURE:R"), this::getSysFeature);
        rules.get("/sys/feature-flags/{feature_flag_id}", authenticate().andAuthorize("SYS_FEATURE_FLAG:R"), this::getSysFeatureFlag);
        rules.get("/sys/features", authenticate().andAuthorize("SYS_FEATURE:R"), this::pageSysFeature);
        rules.get("/sys/feature-flags", authenticate().andAuthorize("SYS_FEATURE_FLAG:R"), this::pageSysFeatureFlag);
        rules.put("/sys/features/{feature_id}", authenticate().andAuthorize("SYS_FEATURE:W"), this::updateSysFeature);
        rules.put("/sys/feature-flags/{feature_flag_id}", authenticate().andAuthorize("SYS_FEATURE_FLAG:W"), this::updateSysFeatureFlag);
    }

    ///
    /// `POST /sys/feature-flags` 新建功能开关
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysFeatureFlag(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/features/{feature_id}` 获取指定功能
    ///
    /// @param request the server request
    /// @param response the server response
    void getSysFeature(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/feature-flags/{feature_flag_id}` 获取指定功能开关
    ///
    /// @param request the server request
    /// @param response the server response
    void getSysFeatureFlag(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/features` 功能分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysFeature(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/feature-flags` 功能开关分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysFeatureFlag(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /sys/features/{feature_id}` 修改功能
    ///
    /// @param request the server request
    /// @param response the server response
    void updateSysFeature(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /sys/feature-flags/{feature_flag_id}` 修改功能开关
    ///
    /// @param request the server request
    /// @param response the server response
    void updateSysFeatureFlag(ServerRequest request, ServerResponse response);

}
