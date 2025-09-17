package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictType;
import cool.houge.mahu.entity.system.query.QDict;
import cool.houge.mahu.entity.system.query.QDictType;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// 数字字典类型
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class DictTypeRepository extends HBeanRepository<String, DictType> {

    public DictTypeRepository(Database db) {
        super(DictType.class, db);
    }

    /// 查询指定代码的数据，如果传入的 `typeCodes` 为 `null` 则返回所有数据
    public List<DictType> findByTypeCodes(Collection<String> typeCode) {
        var qb = new QDictType(db());
        if (typeCode != null && !typeCode.isEmpty()) {
            qb.typeCode.in(typeCode);
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
    /// | type_code | string |
    /// | name | string |
    public PagedList<DictType> findPage(DataFilter dataFilter) {
        return new QDictType(db())
                .also(o -> super.apply(o, dataFilter))
                .data
                .fetch()
                .findPagedList();
    }

    /// 查询指定字典数据
    ///
    /// @param dataCode 字典数据代码
    public Dict findDictData(String dataCode) {
        return new QDict(db()).code.eq(dataCode).findOne();
    }

    @Override
    protected @NonNull List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QDictType.Alias.createdAt),
                FilterItem.of(QDictType.Alias.updatedAt),
                FilterItem.of(QDictType.Alias.typeCode),
                FilterItem.of(QDictType.Alias.name)
                //
                );
    }
}
