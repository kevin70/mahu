package cool.houge.blma.shared.repository;

import cool.houge.mahu.entity.system.Dict;
import io.ebean.BeanRepository;
import io.ebean.Database;
import io.helidon.service.registry.Service;

/// 公共字典数据仓库
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class SharedDictRepository extends BeanRepository<String, Dict> {

    public SharedDictRepository(Database db) {
        super(Dict.class, db);
    }
}
