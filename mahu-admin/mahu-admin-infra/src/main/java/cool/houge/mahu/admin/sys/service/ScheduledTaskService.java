package cool.houge.mahu.admin.sys.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.entity.sys.ScheduledTaskLog;
import cool.houge.mahu.model.query.ScheduledTaskQuery;
import cool.houge.mahu.repository.sys.ScheduledExeLogRepository;
import cool.houge.mahu.repository.sys.ScheduledTaskRepository;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import java.time.Instant;
import java.util.Objects;
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
    private final ScheduledExeLogRepository scheduledExeLogRepository;

    /// 立即执行定时任务
    @Transactional
    public void execute(ScheduledTask entity) {
        var dbEntity = scheduledTaskRepository.findForUpdate(entity.getTaskName());
        if (dbEntity == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到定时任务");
        }
        if (Objects.equals(entity.getPicked(), Boolean.TRUE)) {
            throw new BizCodeException(BizCodes.FAILED_PRECONDITION, "任务不存在或正在执行中");
        }
        dbEntity.setExecutionTime(Instant.now());
        scheduledTaskRepository.update(dbEntity);
        log.debug("立即执行定时任务 [taskName={}]", entity.getTaskName());
    }

    /// 分页查询系统定时任务
    @Transactional(readOnly = true)
    public PagedList<ScheduledTask> findPage(ScheduledTaskQuery query, Page page) {
        return scheduledTaskRepository.findPage(query, page);
    }

    /// 分页查询定时任务执行日志
    @Transactional(readOnly = true)
    public PagedList<ScheduledTaskLog> findPage4ExecutionLog(String taskName, Page page) {
        return scheduledExeLogRepository.findPage(taskName, page);
    }
}
