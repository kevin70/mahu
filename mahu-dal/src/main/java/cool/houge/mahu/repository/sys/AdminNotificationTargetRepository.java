package cool.houge.mahu.repository.sys;

import cool.houge.mahu.entity.sys.AdminNotificationTarget;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.helidon.service.registry.Service;

/// 管理员通知定向目标
@Service.Singleton
public class AdminNotificationTargetRepository extends HBeanRepository<Long, AdminNotificationTarget> {

    public AdminNotificationTargetRepository(Database db) {
        super(AdminNotificationTarget.class, db);
    }
}
