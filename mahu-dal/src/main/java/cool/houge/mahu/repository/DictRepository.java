package cool.houge.mahu.repository;

import cool.houge.mahu.entity.Dict;
import io.ebean.BeanRepository;
import io.ebean.Database;
import io.helidon.service.registry.Service;

/// 公共字典数据仓库
///
/// 当前仅承载基础 CRUD，复杂查询由 `DictGroupRepository` 统一封装。
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class DictRepository extends BeanRepository<Integer, Dict> {

    public DictRepository(Database db) {
        super(Dict.class, db);
    }
}
