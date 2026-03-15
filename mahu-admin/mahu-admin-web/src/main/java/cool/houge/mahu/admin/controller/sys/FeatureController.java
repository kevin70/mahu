package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.oas.controller.HFeatureFlagService;
import cool.houge.mahu.admin.oas.vo.SysFeatureFlagCreateRequest;
import cool.houge.mahu.admin.oas.vo.SysFeatureFlagUpdateRequest;
import cool.houge.mahu.admin.sys.service.FeatureFlagService;
import cool.houge.mahu.shared.query.FeatureFlagQuery;
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
public class FeatureController implements HFeatureFlagService, WebSupport {

    private final SysBeanMapper beanMapper;
    private final FeatureFlagService featureFlagService;

    @Override
    public void pageSysFeatureFlag(ServerRequest request, ServerResponse response) {
        var qb = FeatureFlagQuery.builder();
        queryArg(request, "enabled").map(Boolean::valueOf).ifPresent(qb::enabled);
        queryArg(request, "name").ifPresent(qb::name);
        queryArg(request, "code").ifPresent(qb::code);

        var page = page(request);
        var plist = featureFlagService.findPage(qb.build(), page);
        var rs = beanMapper.toPageResponse(plist, beanMapper::toSysFeatureFlagResponse);
        response.send(rs);
    }

    @Override
    public void getSysFeatureFlag(ServerRequest request, ServerResponse response) {
        var id = pathInt(request, "feature_flag_id");
        var bean = featureFlagService.findById(id);
        var rs = beanMapper.toSysFeatureFlagResponse(bean);
        response.send(rs);
    }

    @Override
    public void createSysFeatureFlag(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysFeatureFlagCreateRequest.class);
        validate(vo);

        var bean = beanMapper.toFeatureFlag(vo);
        featureFlagService.create(bean);
        response.status(Status.NO_CONTENT_204).send();
    }

    @Override
    public void updateSysFeatureFlag(ServerRequest request, ServerResponse response) {
        var id = pathInt(request, "feature_flag_id");
        var vo = request.content().as(SysFeatureFlagUpdateRequest.class);
        validate(vo);

        var bean = beanMapper.toFeatureFlag(vo).setId(id);
        featureFlagService.update(bean);
        response.status(Status.NO_CONTENT_204).send();
    }
}
