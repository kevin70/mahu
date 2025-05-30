package cool.houge.mahu.admin.controller.mart;

import static cool.houge.mahu.admin.Permits.MART_CATEGORY;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.mart.service.CategoryService;
import cool.houge.mahu.admin.oas.model.UpsertMartCategoryRequest;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 产品分类
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class CategoryController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final CategoryService categoryService;

    @Inject
    public CategoryController(VoBeanMapper beanMapper, CategoryService categoryService) {
        this.beanMapper = beanMapper;
        this.categoryService = categoryService;
    }

    @Override
    public void routing(HttpRules rules) {
        rules.get("/p/mart/categories", this::listPMartCategories);

        rules.get("/mart/categories", s(this::listMartCategories, MART_CATEGORY.R));
        rules.post("/mart/categories", s(this::createMartCategory, MART_CATEGORY.W));
    }

    private void listPMartCategories(ServerRequest request, ServerResponse response) {
        var ret = categoryService.findAll().stream()
                .map(beanMapper::toListPMartCategories200ResponseInner)
                .toList();
        response.send(ret);
    }

    private void listMartCategories(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = categoryService.findPage(dataFilter);
        var ret = beanMapper.toPageResponse(
                plist.getList(), plist.getTotalCount(), beanMapper::toGetMartCategoryResponse);
        response.send(ret);
    }

    private void createMartCategory(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertMartCategoryRequest.class);
        validate(vo);

        var bean = beanMapper.toCategory(vo);
        categoryService.save(bean);
        response.status(NO_CONTENT_204).send();
    }
}
