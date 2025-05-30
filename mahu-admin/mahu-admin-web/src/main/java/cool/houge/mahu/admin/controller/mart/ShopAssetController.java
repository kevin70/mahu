package cool.houge.mahu.admin.controller.mart;

import static cool.houge.mahu.admin.Permits.MART_ASSET;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.controller.DynamicSecureHandler;
import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.mart.service.AssetService;
import cool.houge.mahu.admin.oas.model.BatchDeleteShopAssetRequest;
import cool.houge.mahu.admin.oas.model.CreateShopAssetRequest;
import cool.houge.mahu.entity.mart.Asset;
import cool.houge.mahu.entity.mart.Shop;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 商店资源
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ShopAssetController implements WebSupport, HttpService, DynamicSecureHandler {

    private final VoBeanMapper beanMapper;
    private final AssetService assetService;

    @Inject
    public ShopAssetController(VoBeanMapper beanMapper, AssetService assetService) {
        this.beanMapper = beanMapper;
        this.assetService = assetService;
    }

    @Override
    public void routing(HttpRules rules) {
        rules.get("/shops/{shop_id}/assets", s(shopSecure(this::listShopAssets), MART_ASSET.R));

        rules.post("/shops/{shop_id}/assets", s(shopSecure(this::createShopAsset), MART_ASSET.W));
        rules.delete("/shops/{shop_id}/assets", s(shopSecure(this::batchDeleteShopAsset), MART_ASSET.W));
    }

    private void listShopAssets(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var pathParams = request.path().pathParameters();
        var shopId = pathParams.first("shop_id").asInt().get();

        var plist = assetService.findPage(shopId, dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toShopAssetResponse);
        response.send(rs);
    }

    private void createShopAsset(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var shopId = pathParams.first("shop_id").asInt().get();
        var vo = request.content().as(CreateShopAssetRequest.class);
        validate(vo);

        var shop = new Shop().setId(shopId);
        var assets = vo.getUris().stream()
                .map(uri -> new Asset().setShop(shop).setUri(uri))
                .toList();
        assetService.saveAll(assets);

        response.status(NO_CONTENT_204).send();
    }

    private void batchDeleteShopAsset(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var shopId = pathParams.first("shop_id").asInt().get();
        var vo = request.content().as(BatchDeleteShopAssetRequest.class);
        validate(vo);

        assetService.deleteShopAsset(shopId, vo.getAssetIds());
        response.status(NO_CONTENT_204).send();
    }
}
