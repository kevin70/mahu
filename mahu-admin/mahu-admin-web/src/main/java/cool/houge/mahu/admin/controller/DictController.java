package cool.houge.mahu.admin.controller;

import com.google.common.collect.Lists;
import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.system.service.DictService;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 公共字典数据接口
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
        rules.get("/dicts", this::listDicts);
        rules.get("/dicts/{type_code}", this::getDict);
        rules.get("/dicts/{type_code}/{data_code}", this::getDictData);
    }

    private void listDicts(ServerRequest request, ServerResponse response) {
        var queryParams = request.query();
        var includeData = queryParams.first("include_data").asBoolean().orElse(false);

        var list = dictService.findAll();
        var rs = Lists.transform(
                list, (o) -> includeData ? beanMapper.toDictResponse(o) : beanMapper.toDictResponseIgnoreData(o));
        response.send(rs);
    }

    private void getDict(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var typeCode = pathParams.get("type_code");

        var bean = dictService.findByTypeCode(typeCode);
        var rs = beanMapper.toDictResponse(bean);
        response.send(rs);
    }

    private void getDictData(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var typeCode = pathParams.get("type_code");
        var dataCode = pathParams.get("data_code");

        var bean = dictService.findDictData(typeCode, dataCode);
        var rs = beanMapper.toDictDataResponse(bean);
        response.send(rs);
    }
}
