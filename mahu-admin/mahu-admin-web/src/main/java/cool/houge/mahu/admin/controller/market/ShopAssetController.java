package cool.houge.mahu.admin.controller.market;

import cool.houge.mahu.admin.controller.DynamicSecureHandler;
import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.market.service.AssetService;
import cool.houge.mahu.admin.oas.model.BatchDeleteShopAssetRequest;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.http.Status;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.MARKET_ASSET;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 商店资源
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ShopAssetController implements WebSupport, HttpService, DynamicSecureHandler {

    @Inject
    AssetService assetService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/shops/{shop_id}/assets", authz(MARKET_ASSET.R()).wrap(shopSecure(this::listShopAssets)));
        rules.delete("/shops/{shop_id}/assets", authz(MARKET_ASSET.R()).wrap(shopSecure(this::batchDeleteShopAsset)));
    }

    private void listShopAssets(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var pathParams = request.path().pathParameters();
        var shopId = pathParams.first("shop_id").asInt().get();

        var plist = assetService.findPage(shopId, dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetShopAssetResponse);
        response.send(rs);
    }

    private void batchDeleteShopAsset(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(BatchDeleteShopAssetRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var shopId = pathParams.first("shop_id").asInt().get();

        assetService.deleteShopAsset(shopId, vo.getAssetIds());
        response.status(NO_CONTENT_204).send();
    }
}
