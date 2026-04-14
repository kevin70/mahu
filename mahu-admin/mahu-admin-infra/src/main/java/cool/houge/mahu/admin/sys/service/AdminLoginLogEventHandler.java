package cool.houge.mahu.admin.sys.service;

import com.github.f4b6a3.ulid.UlidCreator;
import cool.houge.mahu.admin.event.AdminLoginLogEvent;
import cool.houge.mahu.repository.sys.AdminLoginLogRepository;
import io.ebean.annotation.Transactional;
import io.ebean.annotation.TxType;
import io.helidon.service.registry.Event.AsyncObserver;
import io.helidon.service.registry.Service.Singleton;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// [AdminLoginLogEvent] 事件处理器
///
/// 登录日志属于审计副作用，需要独立事务提交，避免主流程异常导致记录回滚。
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
class AdminLoginLogEventHandler {

    private static final Logger log = LogManager.getLogger();

    final AdminLoginLogRepository adminLoginLogRepository;

    @Transactional(type = TxType.REQUIRES_NEW)
    @AsyncObserver
    void onAdminLoginLogEvent(AdminLoginLogEvent event) {
        try {
            var logEntity = event.log();
            logEntity.setId(UlidCreator.getMonotonicUlid().toUuid());
            adminLoginLogRepository.save(logEntity);
        } catch (RuntimeException e) {
            var logEntity = event.log();
            log.error(
                    "persist_admin_login_log_failed grantType={} clientId={} username={} success={}",
                    logEntity.getGrantType(),
                    logEntity.getClientId(),
                    logEntity.getUsername(),
                    logEntity.getSuccess(),
                    e);
        }
    }
}
