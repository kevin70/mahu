package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.mart.Category;
import cool.houge.mahu.entity.mart.query.QCategory;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

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
        var rsqlCtx = RSQLContext.of(qb)
                .property("create_time", qb.createTime)
                .property("update_time", qb.updateTime)
                .property(qb.name)
                .property(qb.ordering)
                .property("parent_id", qb.parent.id);
        apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }
}
