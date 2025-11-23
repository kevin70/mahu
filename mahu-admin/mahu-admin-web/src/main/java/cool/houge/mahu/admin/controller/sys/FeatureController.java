package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.internal.SysBeanMapper;
import cool.houge.mahu.admin.oas.controller.HFeatureService;
import cool.houge.mahu.admin.sys.service.FeatureService;
import cool.houge.mahu.web.WebSupport;
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
    public void pageSysFeature(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = featureService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toSysFeatureResponse);
        response.send(rs);
    }
}
