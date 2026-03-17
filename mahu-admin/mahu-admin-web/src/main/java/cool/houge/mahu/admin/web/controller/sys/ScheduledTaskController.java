package cool.houge.mahu.admin.web.controller.sys;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.mapping.SysBeanMapper;
import cool.houge.mahu.admin.oas.controller.HScheduledTaskService;
import cool.houge.mahu.admin.sys.service.ScheduledTaskService;
import cool.houge.mahu.query.ScheduledTaskQuery;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class ScheduledTaskController implements HScheduledTaskService, WebSupport {

    private final SysBeanMapper beanMapper;
    private final ScheduledTaskService scheduledTaskService;

    @Override
    public void cancelSysScheduledTask(ServerRequest request, ServerResponse response) {
        //
    }

    @Override
    public void executeSysScheduledTask(ServerRequest request, ServerResponse response) {
        var taskId = taskName(request);
        var entity = beanMapper.toScheduledTask(taskId);
        scheduledTaskService.execute(entity);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void pageSysScheduledTask(ServerRequest request, ServerResponse response) {
        var qb = ScheduledTaskQuery.builder();
        queryArg(request, "task_name").ifPresent(qb::taskName);

        var page = page(request);
        var plist = scheduledTaskService.findPage(qb.build(), page);
        var rs = beanMapper.toPageResponse(plist, beanMapper::toSysScheduledTaskResponse);
        response.send(rs);
    }

    @Override
    public void pageSysScheduledTaskLog(ServerRequest request, ServerResponse response) {
        var taskName = taskName(request);
        var plist = scheduledTaskService.findPage4ExecutionLog(taskName, page(request));
        var rs = beanMapper.toPageResponse(plist, beanMapper::toSysScheduledTaskLogResponse);
        response.send(rs);
    }

    String taskName(ServerRequest request) {
        return pathString(request, "task_name");
    }
}
