package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.ListDictPredefineKindsResponseInner;
import cool.houge.mahu.admin.oas.model.UpsertDictRequest;
import cool.houge.mahu.admin.system.service.DictService;
import cool.houge.mahu.common.web.WebDataFilter;
import cool.houge.mahu.common.web.WebSupport;
import cool.houge.mahu.entity.system.Dict;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Arrays;

import static cool.houge.mahu.admin.Permits.DICT;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DictController implements HttpService, WebSupport {

    @Inject
    DictService dictService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/dicts/predefine-kinds", this::listDictPredefineKinds);

        rules.get("/system/dicts", authz(DICT.R()).wrap(this::listDicts));
        rules.post("/system/dicts", authz(DICT.W()).wrap(this::addDict));
        rules.put("/system/dicts/{id:\\d+}", authz(DICT.W()).wrap(this::updateDict));
        rules.delete("/system/dicts/{id:\\d+}", authz(DICT.W()).wrap(this::deleteDict));
        rules.get("/system/dicts/{id:\\d+}", authz(DICT.R()).wrap(this::getDict));
    }

    private void addDict(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertDictRequest.class);
        validate(vo);

        var bean = beanMapper.toDict(vo);
        dictService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void updateDict(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();
        var vo = request.content().as(UpsertDictRequest.class);
        validate(vo);

        var bean = beanMapper.toDict(vo).setId(id);
        dictService.update(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteDict(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        dictService.deleteById(id);
        response.status(NO_CONTENT_204).send();
    }

    private void getDict(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = dictService.findById(id);
        var rs = beanMapper.toGetDictResponse(bean);
        response.send(rs);
    }

    private void listDicts(ServerRequest request, ServerResponse response) {
        var plist = dictService.findPage(new WebDataFilter(request));
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetDictResponse);
        response.send(rs);
    }

    private void listDictPredefineKinds(ServerRequest request, ServerResponse response) {
        var rs = Arrays.stream(Dict.PredefineKinds.values())
                .map(o -> new ListDictPredefineKindsResponseInner()
                        .setKind(o.name())
                        .setLabel(o.getLabel()))
                .toList();
        response.send(rs);
    }
}
