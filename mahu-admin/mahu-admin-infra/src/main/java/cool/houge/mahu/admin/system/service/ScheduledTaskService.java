package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.system.repository.ScheduledExecutionLogRepository;
import cool.houge.mahu.admin.system.repository.ScheduledTaskRepository;
import cool.houge.mahu.admin.entity.ScheduledTaskExeLog;
import cool.houge.mahu.admin.entity.ScheduledTask;
import cool.houge.mahu.domain.DataFilter;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class ScheduledTaskService {

    private static final Logger log = LogManager.getLogger(ScheduledTaskService.class);

    private final ScheduledTaskRepository scheduledTaskRepository;
    private final ScheduledExecutionLogRepository scheduledExecutionLogRepository;

    /// 立即执行定时任务
    @Transactional
    public void execute(ScheduledTask entity) {
        var dbEntity = scheduledTaskRepository.findForUpdate(entity.getId());
        if (dbEntity == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到定时任务");
        }
        if (dbEntity.isPicked()) {
            throw new BizCodeException(BizCodes.FAILED_PRECONDITION, "任务不存在或正在执行中");
        }
        dbEntity.setExecutionTime(Instant.now());
        scheduledTaskRepository.update(dbEntity);
        log.debug("立即执行定时任务 [taskName={}]", entity.getTaskName());
    }

    /// 分页查询系统定时任务
    @Transactional(readOnly = true)
    public PagedList<ScheduledTask> findPage(DataFilter dataFilter) {
        return scheduledTaskRepository.findPage(dataFilter);
    }

    /// 分页查询定时任务执行日志
    @Transactional(readOnly = true)
    public PagedList<ScheduledTaskExeLog> findPage4ExecutionLog(String taskId, DataFilter dataFilter) {
        return scheduledExecutionLogRepository.findPage(taskId, dataFilter);
    }
}
