package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HFeatureFlagService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/sys/feature-flags", authenticate().andAuthorize("SYS_FEATURE_FLAG:W"), this::createSysFeatureFlag);
        rules.get("/sys/feature-flags/{feature_flag_id}", authenticate().andAuthorize("SYS_FEATURE_FLAG:R"), this::getSysFeatureFlag);
        rules.get("/sys/feature-flags", authenticate().andAuthorize("SYS_FEATURE_FLAG:R"), this::pageSysFeatureFlag);
        rules.put("/sys/feature-flags/{feature_flag_id}", authenticate().andAuthorize("SYS_FEATURE_FLAG:W"), this::updateSysFeatureFlag);
    }

    ///
    /// `POST /sys/feature-flags` 新建功能开关
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysFeatureFlag(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/feature-flags/{feature_flag_id}` 获取指定功能开关
    ///
    /// @param request the server request
    /// @param response the server response
    void getSysFeatureFlag(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/feature-flags` 功能开关分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysFeatureFlag(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /sys/feature-flags/{feature_flag_id}` 修改功能开关
    ///
    /// @param request the server request
    /// @param response the server response
    void updateSysFeatureFlag(ServerRequest request, ServerResponse response);

}
