package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.entity.system.Dict;
import cool.houge.mahu.entity.system.DictType;
import cool.houge.mahu.entity.system.query.QDict;
import cool.houge.mahu.entity.system.query.QDictType;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.DataFilter;
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
        var qb = new QDictType(db());
        var filterFields = List.of(
                FilterItem.of(qb.createdAt),
                FilterItem.of(qb.updatedAt),
                FilterItem.of(qb.typeCode),
                FilterItem.of(qb.name)
                //
                );

        super.apply(qb, dataFilter, filterFields);
        return qb.data.fetch().findPagedList();
    }

    /// 查询指定字典数据
    ///
    /// @param dataCode 字典数据代码
    public Dict findDictData(String dataCode) {
        return new QDict(db()).code.eq(dataCode).findOne();
    }
}
