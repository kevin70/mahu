package cool.houge.mahu.shared.repository.sys;

import cool.houge.mahu.entity.sys.StoredObject;
import cool.houge.mahu.entity.sys.query.QStoredObject;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.helidon.service.registry.Service;
import java.util.List;

/// 存储对象
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class StoredObjectRepository extends HBeanRepository<Long, StoredObject> {

    public StoredObjectRepository(Database db) {
        super(StoredObject.class, db);
    }

    /// 更新状态
    ///
    /// @param ids 资源 IDs
    /// @param type 资源类型
    /// @param newStatus 新的状态
    public int updateStatus(List<Long> ids, StoredObject.Type type, int newStatus) {
        var qb = new QStoredObject(db());
        return qb.id.in(ids).type.eq(type).asUpdate().set(qb.status, newStatus).update();
    }
}
