package cool.houge.mahu.admin.sys.service;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.shared.repository.sys.DelayedTaskRepository;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import lombok.AllArgsConstructor;

/// 延时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class DelayedTaskService {

    private final DelayedTaskRepository delayedTaskRepository;

    /// 分页查询延时任务
    @Transactional(readOnly = true)
    public PagedList<DelayedTask> findPage(String topic, Page page) {
        return delayedTaskRepository.findPage(topic, page);
    }
}
