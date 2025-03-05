package cool.houge.mahu.admin.controller.mart;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.mart.service.CategoryService;
import cool.houge.mahu.admin.oas.model.UpsertMartCategoryRequest;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.MART_CATEGORY;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 产品分类
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class CategoryController implements HttpService, WebSupport {

    @Inject
    VoBeanMapper beanMapper;

    @Inject
    CategoryService categoryService;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/mart/categories", authz(MART_CATEGORY.R()).wrap(this::listMartCategories));
        rules.get("/mart/categories.tree", authz(MART_CATEGORY.R()).wrap(this::treeMartCategories));

        rules.post("/mart/categories", authz(MART_CATEGORY.W()).wrap(this::addMartCategory));
    }

    private void listMartCategories(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = categoryService.findPage(dataFilter);
        var ret = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetMartCategoryResponse);
        response.send(ret);
    }

    private void treeMartCategories(ServerRequest request, ServerResponse response) {
        var list = categoryService.findTreeCategories();
        var ret = list.stream().map(beanMapper::toMartCategory);
        response.send(ret);
    }

    private void addMartCategory(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertMartCategoryRequest.class);
        validate(vo);

        var bean = beanMapper.toCategory(vo);
        categoryService.save(bean);
        response.status(NO_CONTENT_204).send();
    }
}
