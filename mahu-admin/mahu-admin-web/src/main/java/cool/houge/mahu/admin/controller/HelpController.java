package cool.houge.mahu.admin.controller;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.VersionResponse;
import cool.houge.mahu.admin.service.HelpService;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 系统帮助接口
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class HelpController implements HttpService {

    private final VoBeanMapper beanMapper;
    private final HelpService helpService;

    @Inject
    public HelpController(VoBeanMapper beanMapper, HelpService helpService) {
        this.beanMapper = beanMapper;
        this.helpService = helpService;
    }

    @Override
    public void routing(HttpRules rules) {
        rules.get("/version", this::getVersion);
    }

    private void getVersion(ServerRequest request, ServerResponse response) {
        var c = helpService.info();

        var ret = new VersionResponse();
        ret.setName(c.name()).setVersion(c.version());
        response.send(ret);
    }
}
