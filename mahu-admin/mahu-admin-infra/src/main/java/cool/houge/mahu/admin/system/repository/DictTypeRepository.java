package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.system.DictData;
import cool.houge.mahu.entity.system.DictType;
import cool.houge.mahu.entity.system.query.QDictData;
import cool.houge.mahu.entity.system.query.QDictType;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

/// 数字字典类型
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DictTypeRepository extends HBeanRepository<String, DictType> {

    public DictTypeRepository(Database db) {
        super(DictType.class, db);
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
        var rsqlCtx = RSQLContext.of(qb)
                .property("created_at", qb.createdAt)
                .property("updated_at", qb.updatedAt)
                .property(qb.typeCode)
                .property(qb.name);
        super.apply(dataFilter, rsqlCtx);
        return qb.data.fetch().findPagedList();
    }

    /// 查询指定字典数据
    ///
    /// @param typeCode 字典类型代码
    /// @param dataCode 字典数据代码
    public DictData findDictData(String typeCode, String dataCode) {
        return new QDictData(db())
                .dictType
                .typeCode
                .eq(typeCode)
                .dataCode
                .eq(dataCode)
                .findOne();
    }
}
