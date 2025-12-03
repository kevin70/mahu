package cool.houge.mahu.admin.controller.sys;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.oas.controller.HScheduledTaskService;
import cool.houge.mahu.admin.sys.service.ScheduledTaskService;
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
        var dataFilter = dataFilter(request);
        var plist = scheduledTaskService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toSysScheduledTaskResponse);
        response.send(rs);
    }

    @Override
    public void pageSysScheduledTaskExe(ServerRequest request, ServerResponse response) {
        var taskId = taskName(request);
        var dataFilter = dataFilter(request);
        var plist = scheduledTaskService.findPage4ExecutionLog(taskId, dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toSysScheduledTaskExeResponse);
        response.send(rs);
    }

    String taskName(ServerRequest request) {
        return pathArg(request, "task_name").get();
    }
}
