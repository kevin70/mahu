package cool.houge.mahu.admin.controller;

import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Singleton;

/// 系统基础接口
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class SettingsController implements HttpService, WebSupport {

    @Override
    public void routing(HttpRules rules) {
        rules.post("/settings/oss-direct-upload", authz().wrap(this::makeSettingsOssDirectUpload));
    }

    private void makeSettingsOssDirectUpload(ServerRequest request, ServerResponse response) {
        //
    }
}
