package cool.houge.mahu.admin.controller.sys;

import static io.helidon.http.Status.FOUND_302;

import cool.houge.mahu.admin.oas.controller.HIdPhotoService;
import cool.houge.mahu.admin.oas.vo.IdPhotoCreatePresignedRequest;
import cool.houge.mahu.entity.sys.IdPhoto;
import cool.houge.mahu.shared.service.OssSharedService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.http.HeaderNames;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 证件照上传
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class IdPhotoController implements HIdPhotoService, WebSupport {

    private final SysBeanMapper beanMapper;
    private final OssSharedService sharedOssService;

    @Override
    public void createIdPhotoPresigned(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(IdPhotoCreatePresignedRequest.class);
        validate(vo);

        var type = IdPhoto.Type.ofIndex(vo.getType());
        var payload = beanMapper.toPresignedUploadPayload(vo);
        var result = sharedOssService.presignedUpload(type, payload);
        response.send(beanMapper.toIdPhotoCreatePresignedResponse(result));
    }

    @Override
    public void forwardIdPhoto(ServerRequest request, ServerResponse response) {
        var idPhotoId = pathString(request, "id_photo_id");
        var location = sharedOssService.presignedGetUrlByIdPhoto(idPhotoId);
        response.status(FOUND_302).header(HeaderNames.LOCATION, location).send();
    }
}
