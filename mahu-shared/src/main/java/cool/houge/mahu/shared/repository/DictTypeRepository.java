package cool.houge.mahu.shared.repository;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictType;
import cool.houge.mahu.entity.query.QDict;
import cool.houge.mahu.entity.query.QDictType;
import cool.houge.mahu.shared.query.DictQuery;
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

    /// 查询指定代码的数据，如果传入的 `ids` 为 `null` 则返回所有数据
    public List<DictType> findByIds(Collection<String> ids) {
        var qb = new QDictType(db());
        if (ids != null && !ids.isEmpty()) {
            qb.id.in(ids);
        }
        return qb.findList();
    }

    /// 分页查询
    public PagedList<DictType> findPage(DictQuery query, Page page) {
        var qb = new QDictType(db());
        qb.id.eqIfPresent(query.getId());
        if (query.getDc() != null) {
            qb.data.dc.eq(query.getDc());
        }

        qb.updatedAt.desc();
        return findPage(qb, page);
    }

    /// 查询指定字典数据
    ///
    /// @param dc 字典数据代码
    public Dict findDictData(Integer dc) {
        return new QDict(db()).dc.eq(dc).findOne();
    }

    /// 加载所有字典数据
    public List<DictType> findAllData() {
        return new QDictType(db()).fetch("data").findList();
    }
}
