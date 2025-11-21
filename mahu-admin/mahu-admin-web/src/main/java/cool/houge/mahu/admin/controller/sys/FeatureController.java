package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.oas.controller.HFeatureService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

///
@Service.Singleton
public class FeatureController implements HFeatureService, WebSupport {

    @Override
    public void pageSysFeature(ServerRequest request, ServerResponse response) {
        //
    }
}
