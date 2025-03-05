package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.system.Dict;
import cool.houge.mahu.entity.system.query.QDict;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

/// 字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DictRepository extends HBeanRepository<Integer, Dict> {

    public DictRepository(Database db) {
        super(Dict.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | updated_at | date-time |
    /// | kind | string |
    /// | slug | string |
    /// | ordering | int |
    public PagedList<Dict> findPage(DataFilter dataFilter) {
        var qb = new QDict(db());
        var rsqlCtx = RSQLContext.of(qb)
                .property("created_at", qb.createdAt)
                .property("updated_at", qb.updatedAt)
                .property(qb.kind)
                .property(qb.slug)
                .property(qb.ordering);
        super.apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }

    public Dict findDictByKindValue(String kind, String value) {
        var qb = new QDict(db());
        qb.kind.eq(kind).value.eq(value);
        return qb.findOne();
    }
}
