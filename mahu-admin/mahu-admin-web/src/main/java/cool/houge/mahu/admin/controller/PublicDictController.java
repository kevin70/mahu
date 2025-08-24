package cool.houge.mahu.admin.controller;

import com.google.common.collect.Lists;
import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.system.service.DictService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

/// 公共字典数据接口
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class PublicDictController implements WebSupport {

    private final VoBeanMapper beanMapper;
    private final DictService dictService;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/p/dicts", this::listPublicDicts);
        rules.get("/p/dicts/{code}", this::getPublicDict);
    }

    private void listPublicDicts(ServerRequest request, ServerResponse response) {
        var queryParams = request.query();
        var typeCode = queryParams.all("type_code", List::of).stream()
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toSet());
        var includeData = queryParams.first("include_data").asBoolean().orElse(false);

        var list = dictService.findByTypeCodes(typeCode);
        var rs = Lists.transform(list, (o) -> beanMapper.toPublicDictTypeResponse(o, includeData));
        response.send(rs);
    }

    private void getPublicDict(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var dataCode = pathParams.get("code");

        var bean = dictService.findDictData(dataCode);
        var rs = beanMapper.toPublicDictDataResponse(bean);
        response.send(rs);
    }
}
