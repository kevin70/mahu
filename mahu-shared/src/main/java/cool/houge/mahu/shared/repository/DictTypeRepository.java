package cool.houge.mahu.shared.repository;

import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictType;
import cool.houge.mahu.entity.query.QDict;
import cool.houge.mahu.entity.query.QDictType;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import java.util.Collection;
import java.util.List;

/// 数字字典类型
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class DictTypeRepository extends HBeanRepository<Integer, DictType> {

    public DictTypeRepository(Database db) {
        super(DictType.class, db);
    }

    /// 查询指定代码的数据，如果传入的 `ids` 为 `null` 则返回所有数据
    public List<DictType> findByIds(Collection<Integer> ids) {
        var qb = new QDictType(db());
        if (ids != null && !ids.isEmpty()) {
            qb.id.in(ids);
        }
        return qb.findList();
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | updated_at | date-time |
    /// | id | string |
    /// | name | string |
    public PagedList<DictType> findPage(DataFilter dataFilter) {
        return new QDictType(db())
                .also(o -> super.apply(o.query(), dataFilter, filterableItems()))
                .data
                .fetch()
                .findPagedList();
    }

    /// 查询指定字典数据
    ///
    /// @param dc 字典数据代码
    public Dict findDictData(Integer dc) {
        return new QDict(db()).dc.eq(dc).findOne();
    }

    /// 加载所有字典数据
    public List<DictType> findAllData() {
        return new QDictType(db()).data.fetch().findList();
    }

    List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QDictType.Alias.createdAt),
                FilterItem.of(QDictType.Alias.updatedAt),
                FilterItem.of(QDictType.Alias.id),
                FilterItem.of(QDictType.Alias.name)
                //
                );
    }
}
