package cool.houge.mahu.repository;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.entity.query.QDict;
import cool.houge.mahu.entity.query.QDictGroup;
import cool.houge.mahu.model.query.DictGroupQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import java.util.Collection;
import java.util.List;

/// 数字字典分组
///
/// 约定：分组主键为 `id`，组内字典项通过 `data` 关联。
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class DictGroupRepository extends HBeanRepository<String, DictGroup> {

    public DictGroupRepository(Database db) {
        super(DictGroup.class, db);
    }

    /// 查询指定分组 ID 集合。
    ///
    /// 行为说明：
    /// - `ids == null` 或空集合时，不加 ID 过滤，返回全部分组。
    /// - 传入非空集合时，仅返回命中的分组。
    public List<DictGroup> findByIds(Collection<String> ids) {
        var qb = new QDictGroup(db());
        if (ids != null && !ids.isEmpty()) {
            qb.id.in(ids);
        }
        return qb.findList();
    }

    /// 分页查询分组
    ///
    /// 过滤规则：
    /// - `groupId` 命中过滤分组主键
    /// - `dc` 命中过滤关联字典项编码
    ///
    /// 排序规则：按 `id DESC` 返回，保证分页顺序稳定。
    public PagedList<DictGroup> findPage(DictGroupQuery query, Page page) {
        var qb = new QDictGroup(db());
        qb.id.eqIfPresent(query.getGroupId());
        if (query.getDc() != null) {
            qb.data.dc.eq(query.getDc());
        }

        qb.id.desc();
        return findPage(qb, page);
    }

    /// 查询单个字典项
    ///
    /// @param dc 字典数据代码
    /// @return 命中返回对应字典项，未命中返回 null
    public Dict findDictData(Integer dc) {
        return new QDict(db()).dc.eq(dc).findOne();
    }

    /// 查询所有分组并预加载 `data`
    ///
    /// 适用于一次性加载分组及其字典项，减少后续按组逐条加载。
    public List<DictGroup> findAllData() {
        return new QDictGroup(db()).fetch("data").findList();
    }
}
