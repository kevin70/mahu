package cool.houge.mahu.controller;

import cool.houge.mahu.common.web.WebSupport;
import cool.houge.mahu.internal.VoBeanMapper;
import cool.houge.mahu.system.service.DictService;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 字典
///
/// @author ZY (kzou227@qq.com
@Singleton
public class DictController implements HttpService, WebSupport {

    @Inject
    DictService dictService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/dicts", this::listDicts);
        rules.get("/dicts/{slug}", this::getDict);
    }

    private void listDicts(ServerRequest request, ServerResponse response) {
        var kind = request.query().first("kind").orElse(null);
        var list = dictService.findByKind(kind);
        var rs = list.stream().map(beanMapper::toGetDictResponse).toList();
        response.send(rs);
    }

    private void getDict(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var slug = pathParams.get("slug");

        var bean = dictService.obtainBySlug(slug);
        var rs = beanMapper.toGetDictResponse(bean);
        response.send(rs);
    }
}
