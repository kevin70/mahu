package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permissions.DICT;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertDictRequest;
import cool.houge.mahu.admin.system.service.DictService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class DictController implements WebSupport {

    private final VoBeanMapper beanMapper;
    private final DictService dictService;

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/dicts", s(this::pageSystemDicts, DICT.R));
        rules.get("/system/dicts/{type_code}", s(this::getSystemDictType, DICT.R));

        rules.post("/system/dicts", s(this::createSystemDictType, DICT.W));
        rules.put("/system/dicts/{type_code}", s(this::updateSystemDictType, DICT.W));
        rules.delete("/system/dicts/{type_code}", s(this::deleteSystemDictType, DICT.W));
        rules.post("/system/dicts/{type_code}", s(this::addSystemDictData, DICT.W));
    }

    private void pageSystemDicts(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = dictService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toDictResponse);
        response.send(rs);
    }

    private void getSystemDictType(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var typeCode = pathParams.get("type_code");

        var bean = dictService.findById(typeCode);
        var rs = beanMapper.toDictResponse(bean);
        response.send(rs);
    }

    private void createSystemDictType(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertDictRequest.class);
        validate(vo);

        var bean = beanMapper.toDictType(vo);
        dictService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void updateSystemDictType(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var vo = request.content().as(UpsertDictRequest.class).setTypeCode(pathParams.get("type_code"));
        validate(vo);

        var bean = beanMapper.toDictType(vo);
        dictService.update(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteSystemDictType(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var typeCode = pathParams.get("type_code");
        dictService.deleteByTypeCode(typeCode);
        response.status(NO_CONTENT_204).send();
    }

    private void addSystemDictData(ServerRequest request, ServerResponse response) {
        //
    }
}
