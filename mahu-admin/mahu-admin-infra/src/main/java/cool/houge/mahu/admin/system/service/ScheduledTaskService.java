package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.system.repository.ScheduledTaskRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.ScheduledTask;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledTaskService {

    @Inject
    ScheduledTaskRepository scheduledTaskRepository;

    public PagedList<ScheduledTask> findPage(DataFilter dataFilter) {
        return scheduledTaskRepository.findPage(dataFilter);
    }
}
