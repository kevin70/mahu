package cool.houge.mahu.shared.repository.sys;

import cool.houge.mahu.entity.sys.Asset;
import cool.houge.mahu.entity.sys.query.QAsset;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.helidon.service.registry.Service;
import java.util.List;

/// 资源文件
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class AssetRepository extends HBeanRepository<Long, Asset> {

    public AssetRepository(Database db) {
        super(Asset.class, db);
    }

    /// 更新状态
    ///
    /// @param ids 资源 IDs
    /// @param type 资源类型
    /// @param newStatus 新的状态
    public int updateStatus(List<Long> ids, Asset.Type type, int newStatus) {
        var qb = new QAsset(db());
        return qb.id.in(ids).type.eq(type).asUpdate().set(qb.status, newStatus).update();
    }
}
