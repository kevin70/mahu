package cool.houge.mahu.admin.mart.service;

import cool.houge.mahu.admin.mart.repository.CategoryRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.mart.Category;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

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

    /// 查询所有分类
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
