package cool.houge.mahu.shared.repository.sys;

import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.sys.Feature;
import cool.houge.mahu.entity.sys.query.QFeature;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import java.util.List;

/// 系统功能配置
@Service.Singleton
public class FeatureRepository extends HBeanRepository<Integer, Feature> {

    public FeatureRepository(Database db) {
        super(Feature.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | updated_at | date-time |
    /// | status | int32 |
    /// | module | string |
    /// | code | string |
    /// | name | string |
    public PagedList<Feature> findPage(DataFilter dataFilter) {
        return new QFeature(db())
                .also(o -> super.apply(o.query(), dataFilter, filterableItems()))
                .findPagedList();
    }

    List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QFeature.Alias.createdAt),
                FilterItem.of(QFeature.Alias.updatedAt),
                FilterItem.of(QFeature.Alias.status),
                FilterItem.of(QFeature.Alias.module),
                FilterItem.of(QFeature.Alias.code),
                FilterItem.of(QFeature.Alias.name)
                //
                );
    }
}
