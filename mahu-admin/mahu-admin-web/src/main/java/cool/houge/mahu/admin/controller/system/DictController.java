package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permits.DICT;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertDictRequest;
import cool.houge.mahu.admin.system.service.DictService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DictController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final DictService dictService;

    @Inject
    public DictController(VoBeanMapper beanMapper, DictService dictService) {
        this.beanMapper = beanMapper;
        this.dictService = dictService;
    }

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/dicts", authz(DICT.R()).wrap(this::listSystemDicts));
        rules.get("/system/dicts/{type_code}", authz(DICT.R()).wrap(this::getSystemDict));

        rules.post("/system/dicts", authz(DICT.W()).wrap(this::createSystemDict));
        rules.put("/system/dicts/{type_code}", authz(DICT.W()).wrap(this::updateSystemDict));
    }

    private void listSystemDicts(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = dictService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toDictResponse);
        response.send(rs);
    }

    private void getSystemDict(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var typeCode = pathParams.get("type_code");

        var bean = dictService.findByTypeCode(typeCode);
        var rs = beanMapper.toDictResponse(bean);
        response.send(rs);
    }

    private void createSystemDict(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertDictRequest.class);
        validate(vo);

        var bean = beanMapper.toDictType(vo);
        dictService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void updateSystemDict(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var vo = request.content().as(UpsertDictRequest.class).setTypeCode(pathParams.get("type_code"));
        validate(vo);

        var bean = beanMapper.toDictType(vo);
        dictService.update(bean);
        response.status(NO_CONTENT_204).send();
    }
}
