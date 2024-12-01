package cool.houge.mahu.controller;

import com.google.common.primitives.Ints;
import cool.houge.mahu.common.web.WebSupport;
import cool.houge.mahu.internal.VoBeanMapper;
import cool.houge.mahu.service.RegionService;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.function.Predicate;

/// 行政区
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RegionController implements HttpService, WebSupport {

    @Inject
    RegionService regionService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/regions", this::listRegions);
        rules.get("/regions/{code}", this::getRegion);
    }

    private void listRegions(ServerRequest request, ServerResponse response) {
        var queryParams = request.query();
        var depth = queryParams
                .first("depth")
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .map(Ints::tryParse)
                .orElse(3);
        var list = regionService.findDepthBound(depth);
        response.send(list.stream().map(beanMapper::toGetRegionResponse).toList());
    }

    private void getRegion(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var code = pathParams.get("code");

        var region = regionService.loadRegionFully(code);
        var rs = beanMapper.toGetRegionResponse(region);
        response.send(rs);
    }
}
