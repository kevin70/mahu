package cool.houge.mahu.admin.controller;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.GetPresignedUploadRequest;
import cool.houge.mahu.admin.oas.model.GetPresignedUploadResponse;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.service.OssService;
import cool.houge.mahu.admin.service.PresignedUploadPayload;
import cool.houge.mahu.config.OssKind;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Singleton;

/// 对象存储
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class OssController implements WebSupport, HttpService {

    private final VoBeanMapper beanMapper;
    private final OssService ossService;

    public OssController(VoBeanMapper beanMapper, OssService ossService) {
        this.beanMapper = beanMapper;
        this.ossService = ossService;
    }

    @Override
    public void routing(HttpRules rules) {
        rules.post("/oss/presigned-upload", authz().wrap(this::getPresignedUpload));
    }

    private void getPresignedUpload(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(GetPresignedUploadRequest.class);
        PresignedUploadPayload payload;
        if (OssKind.ADMIN_AVATAR.matches(vo.getKind())) {
            var bean = beanMapper.toGetPresignedUploadAdminAvatarForm(vo);
            validate(bean);
            payload = beanMapper
                    .toPresignedUploadPayload(bean)
                    .setAdminId(AuthContext.current().uid());
        } else if (OssKind.SHOP_ASSET.matches(vo.getKind())) {
            var bean = beanMapper.toGetPresignedUploadShopAssetForm(vo);
            validate(bean);
            payload = beanMapper.toPresignedUploadPayload(bean);
        } else {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "不支持的上传类型 kind: " + vo.getKind());
        }

        var url = ossService.presignedUpload(payload);
        var rs = new GetPresignedUploadResponse();
        rs.setPresignedUploadUrl(url);
        response.send(rs);
    }
}
