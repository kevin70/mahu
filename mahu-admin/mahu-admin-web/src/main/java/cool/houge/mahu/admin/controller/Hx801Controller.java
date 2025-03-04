package cool.houge.mahu.admin.controller;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.AddHx801LogRequest;
import cool.houge.mahu.admin.service.Hx801Service;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static io.helidon.http.Status.NO_CONTENT_204;

/// HX801 日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class Hx801Controller implements HttpService, WebSupport {

    @Inject
    VoBeanMapper beanMapper;

    @Inject
    Hx801Service hx801Service;

    @Override
    public void routing(HttpRules rules) {
        rules.post("/hx801", this::addHx801Log);
    }

    private void addHx801Log(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(AddHx801LogRequest.class);
        validate(vo);
        var bean = beanMapper.toHx801Log(vo);
        hx801Service.save(bean);

        response.status(NO_CONTENT_204).send();
    }
}
