package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.oas.controller.HAssetService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 文件上传
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class AssetController implements HAssetService, WebSupport {

    @Override
    public void createAssetPresigned(ServerRequest request, ServerResponse response) {
        //
    }

    @Override
    public void forwardAsset(ServerRequest request, ServerResponse response) {
        //
    }
}
