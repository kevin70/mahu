package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.oas.controller.HFileService;
import cool.houge.mahu.admin.oas.vo.FileCreatePresignedRequest;
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
public class FileController implements HFileService, WebSupport {

    @Override
    public void createFilePresigned(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(FileCreatePresignedRequest.class);
        validate(vo);
    }

    @Override
    public void forwardFile(ServerRequest request, ServerResponse response) {
        //
    }
}
