package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.FilterField;
import cool.houge.mahu.entity.mart.Category;
import cool.houge.mahu.entity.mart.query.QCategory;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

import java.util.List;

/// 产品分类
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class CategoryRepository extends HBeanRepository<Integer, Category> {

    public CategoryRepository(Database db) {
        super(Category.class, db);
    }

    /// 分页查询
    public PagedList<Category> findPage(DataFilter dataFilter) {
        var qb = new QCategory(db());
        var filterFields = List.of(
                FF_CREATED_AT,
                FF_UPDATED_AT,
                FilterField.with(qb.name).build(),
                FilterField.with(qb.ordering).build(),
                FilterField.with(qb.parent.id).filterName("parent_id").build()
                //
                );
        super.apply(dataFilter, filterFields, qb.query());
        return qb.findPagedList();
    }
}
