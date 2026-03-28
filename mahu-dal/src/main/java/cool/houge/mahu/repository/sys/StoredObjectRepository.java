package cool.houge.mahu.repository.sys;

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
public class StoredObjectRepository extends HBeanRepository<String, StoredObject> {

    public StoredObjectRepository(Database db) {
        super(StoredObject.class, db);
    }

    /// 批量更新对象状态
    ///
    /// 命中规则：`id in (:ids)` 且 `type = :type`。
    ///
    /// @param ids 资源 IDs
    /// @param type 资源类型
    /// @param newStatus 新的状态
    /// @return 实际被更新的记录数
    public int updateStatus(List<String> ids, StoredObject.Type type, int newStatus) {
        var qb = new QStoredObject(db());
        return qb.id.in(ids).type.eq(type).asUpdate().set(qb.status, newStatus).update();
    }
}
