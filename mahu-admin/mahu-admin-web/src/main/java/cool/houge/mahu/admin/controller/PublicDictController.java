package cool.houge.mahu.admin.controller;

import static cool.houge.mahu.web.ServerRequestUtils.pathInt;

import com.google.common.collect.Lists;
import cool.houge.mahu.admin.oas.controller.HPublicDictService;
import cool.houge.mahu.admin.sys.service.DictService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
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
public class PublicDictController implements HPublicDictService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final DictService dictService;

    @Override
    public void getPublicDict(ServerRequest request, ServerResponse response) {
        var dc = pathInt(request, "dc");

        var bean = dictService.findDictData(dc);
        var rs = beanMapper.toPublicDictDataResponse(bean);
        response.send(rs);
    }

    @Override
    public void listPublicDict(ServerRequest request, ServerResponse response) {
        var queryParams = request.query();
        var idList = queryParams.all("type_id", List::of).stream()
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toSet());
        var includeData = queryParams.first("include_data").asBoolean().orElse(false);

        var list = dictService.findByIds(idList);
        var rs = Lists.transform(list, (o) -> beanMapper.toPublicDictTypeResponse(o, includeData));
        response.send(rs);
    }
}
