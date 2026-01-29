package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.oas.controller.HFeatureService;
import cool.houge.mahu.admin.oas.vo.SysFeatureUpdateRequest;
import cool.houge.mahu.admin.sys.service.FeatureService;
import cool.houge.mahu.shared.query.FeatureQuery;
import cool.houge.mahu.web.WebSupport;
import io.helidon.http.Status;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 系统特征服务
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class FeatureController implements HFeatureService, WebSupport {

    private final SysBeanMapper beanMapper;
    private final FeatureService featureService;

    @Override
    public void getSysFeature(ServerRequest request, ServerResponse response) {
        var featureId = pathInt(request, "feature_id");
        var bean = featureService.findById(featureId);
        var rs = beanMapper.toSysFeatureResponse(bean);
        response.send(rs);
    }

    @Override
    public void pageSysFeature(ServerRequest request, ServerResponse response) {
        var qb = FeatureQuery.builder();
        queryIntArgs(request, "status").ifPresent(qb::statusList);
        queryArg(request, "name").ifPresent(qb::name);

        var page = page(request);
        var plist = featureService.findPage(qb.build(), page);
        var rs = beanMapper.toPageResponse(plist, beanMapper::toSysFeatureResponse);
        response.send(rs);
    }

    @Override
    public void updateSysFeature(ServerRequest request, ServerResponse response) {
        var featureId = pathInt(request, "feature_id");
        var vo = request.content().as(SysFeatureUpdateRequest.class);
        validate(vo);

        var bean = beanMapper.toFeature(vo).setId(featureId);
        featureService.update(bean);
        response.status(Status.NO_CONTENT_204).send();
    }
}
