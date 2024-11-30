package cool.houge.mahu.system.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.system.Dict;
import cool.houge.mahu.entity.system.query.QDict;
import io.ebean.Database;
import jakarta.inject.Singleton;

import java.util.List;

/// 字典
///
/// @author ZY (kzou227@qq.com
@Singleton
public class DictRepository extends HBeanRepository<String, Dict> {

    public DictRepository(Database db) {
        super(Dict.class, db);
    }

    /// 查询指定字典类型的数据
    public List<Dict> findByKind(String kind) {
        return new QDict(db()).kind.eqIfPresent(kind).orderBy().ordering.desc().findList();
    }

    /// 查询指定 SLUG 的字典数据
    public Dict findBySlug(String slug) {
        return new QDict(db()).slug.eqIfPresent(slug).findOne();
    }
}
