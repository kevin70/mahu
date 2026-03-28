package cool.houge.mahu.repository.sys;

import cool.houge.mahu.entity.sys.IdPhoto;
import cool.houge.mahu.entity.sys.query.QIdPhoto;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.helidon.service.registry.Service;
import java.util.List;

/// 证件照
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class IdPhotoRepository extends HBeanRepository<String, IdPhoto> {

    public IdPhotoRepository(Database db) {
        super(IdPhoto.class, db);
    }

    /// 批量更新证件照状态
    ///
    /// 命中规则：`id in (:ids)` 且 `type = :type`。
    ///
    /// @param ids 资源 IDs
    /// @param type 资源类型
    /// @param newStatus 新的状态
    /// @return 实际被更新的记录数
    public int updateStatus(List<String> ids, IdPhoto.Type type, int newStatus) {
        var qb = new QIdPhoto(db());
        return qb.id.in(ids).type.eq(type).asUpdate().set(qb.status, newStatus).update();
    }
}
