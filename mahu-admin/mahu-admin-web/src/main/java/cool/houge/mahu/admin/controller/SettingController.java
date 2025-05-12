package cool.houge.mahu.admin.controller;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.MakeOssDirectUploadRequest;
import cool.houge.mahu.admin.service.SettingService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 系统基础接口
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class SettingController implements HttpService, WebSupport {

    @Inject
    SettingService settingService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.post("/settings/oss-direct-upload", authz().wrap(this::makeOssDirectUpload));
    }

    private void makeOssDirectUpload(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(MakeOssDirectUploadRequest.class);
        validate(vo);

        var payload = beanMapper.toMakeOssDirectUploadPayload(vo);
        var bean = settingService.makeOssDirectUpload(payload);
        var rs = beanMapper.toMakeOssDirectUploadResponse(bean);
        response.send(rs);
    }
}
