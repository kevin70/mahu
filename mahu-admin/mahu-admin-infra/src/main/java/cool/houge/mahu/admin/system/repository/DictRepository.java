package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
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

    public PagedList<Dict> findPage(DataFilter dataFilter) {
        var qb = new QDict(db());
        return findPagedList(qb.query(), dataFilter);
    }

    public Dict findDictByKindValue(String kind, String value) {
        var qb = new QDict(db());
        qb.kind.eq(kind).value.eq(value);
        return qb.findOne();
    }
}
