package cool.houge.mahu.admin.sys.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cool.houge.mahu.admin.event.AdminAccessEvent;
import cool.houge.mahu.admin.sys.repository.AdminAccessLogRepository;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Event.Observer;
import io.helidon.service.registry.Service.Singleton;
import lombok.AllArgsConstructor;

/// [AdminAccessEvent] 事件处理器
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
class AdminAccessEventHandler {

    final AdminAccessLogRepository adminAccessLogRepository;

    @Transactional
    @Observer
    void onAdminAccessLogEvent(AdminAccessEvent event) {
        var logEntity = event.log();
        logEntity.setId(UuidCreator.getTimeOrderedEpoch());
        adminAccessLogRepository.save(logEntity);
    }
}
