package cool.houge.mahu.admin.controller.sys;

import static cool.houge.mahu.web.ServerRequestUtils.pathLong;
import static io.helidon.http.Status.FOUND_302;

import cool.houge.mahu.admin.oas.controller.HFileService;
import cool.houge.mahu.admin.oas.vo.FileCreatePresignedRequest;
import cool.houge.mahu.entity.sys.StoredObject;
import cool.houge.mahu.shared.service.SharedOssService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.http.HeaderNames;
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

    private final SysBeanMapper beanMapper;
    private final SharedOssService sharedOssService;

    @Override
    public void createFilePresigned(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(FileCreatePresignedRequest.class);
        validate(vo);

        var type = StoredObject.Type.ofIndex(vo.getType());
        var payload = beanMapper.toPresignedUploadPayload(vo);
        var result = sharedOssService.presignedUpload(type, payload);
        response.send(beanMapper.toFileCreatePresignedResponse(result));
    }

    @Override
    public void forwardFile(ServerRequest request, ServerResponse response) {
        var fileId = pathString(request, "file_id");
        var location = sharedOssService.presignedGetUrlByStoredObject(fileId);
        response.status(FOUND_302).header(HeaderNames.LOCATION, location).send();
    }
}
