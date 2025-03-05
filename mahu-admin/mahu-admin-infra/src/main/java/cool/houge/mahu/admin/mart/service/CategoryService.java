package cool.houge.mahu.admin.mart.service;

import cool.houge.mahu.admin.mart.repository.CategoryRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.mart.Category;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

/// 产品分类
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    /// 保存分类
    @Transactional
    public void save(Category entity) {
        categoryRepository.save(entity);
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Category> findPage(DataFilter dataFilter) {
        return categoryRepository.findPage(dataFilter);
    }

    /// 查询分类树形结构
    @Transactional(readOnly = true)
    public List<Category> findTreeCategories() {
        var all = categoryRepository.findAll();

        var list = new ArrayList<Category>();
        arrange(all, null, list);
        return list;
    }

    void arrange(List<Category> all, Category parent, List<Category> list) {
        var pid = ofNullable(parent).map(Category::getId).orElse(null);
        for (Category bean : all) {
            var parentId = ofNullable(bean.getParent()).map(Category::getId).orElse(null);
            if (Objects.equals(parentId, pid)) {
                if (parent == null) {
                    list.add(bean);
                } else {
                    var children = ofNullable(parent.getChildren()).orElseGet(ArrayList::new);
                    children.add(bean);
                    parent.setChildren(children);
                }
                arrange(all, bean, list);
            }
        }
    }
}
