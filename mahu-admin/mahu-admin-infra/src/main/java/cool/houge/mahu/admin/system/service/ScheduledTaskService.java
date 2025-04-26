package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.system.repository.ScheduledExecutionLogRepository;
import cool.houge.mahu.admin.system.repository.ScheduledTaskRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.log.ScheduledExecutionLog;
import cool.houge.mahu.entity.system.ScheduledTask;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledTaskService {

    @Inject
    ScheduledTaskRepository scheduledTaskRepository;

    @Inject
    ScheduledExecutionLogRepository scheduledExecutionLogRepository;

    /// 立即执行定时任务
    @Transactional
    public void execute(ScheduledTask entity) {
        var b = scheduledTaskRepository.execute(entity);
        if (!b) {
            throw new BizCodeException(BizCodes.FAILED_PRECONDITION, "任务不存在或正在执行中");
        }
    }

    /// 分页查询系统定时任务
    @Transactional(readOnly = true)
    public PagedList<ScheduledTask> findPage(DataFilter dataFilter) {
        return scheduledTaskRepository.findPage(dataFilter);
    }

    /// 分页查询定时任务执行日志
    @Transactional(readOnly = true)
    public PagedList<ScheduledExecutionLog> findPage4ExecutionLog(
            String taskName, String taskInstance, DataFilter dataFilter) {
        return scheduledExecutionLogRepository.findPage(taskName, taskInstance, dataFilter);
    }
}
