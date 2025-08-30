package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permissions.SCHEDULED_TASK;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.system.service.ScheduledTaskService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class ScheduledTaskController implements WebSupport, HttpService {

    private final VoBeanMapper beanMapper;
    private final ScheduledTaskService scheduledTaskService;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/scheduled-tasks", s(this::pageSystemScheduledTasks, SCHEDULED_TASK.R));
        rules.get("/system/scheduled-tasks/{task_id}/executions", s(this::pageSystemScheduledTaskExecutions, SCHEDULED_TASK.R));
        rules.post("/system/scheduled-tasks/{task_id}/executions", s(this::executeSystemScheduledTask, SCHEDULED_TASK.W));
    }

    private void pageSystemScheduledTasks(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = scheduledTaskService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toScheduledTaskResponse);
        response.send(rs);
    }

    private void pageSystemScheduledTaskExecutions(ServerRequest request, ServerResponse response) {
        var taskId = taskId(request);
        var dataFilter = dataFilter(request);
        var plist = scheduledTaskService.findPage4ExecutionLog(taskId, dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toScheduledTaskExecutionResponse);
        response.send(rs);
    }

    private void executeSystemScheduledTask(ServerRequest request, ServerResponse response) {
        var taskId = taskId(request);
        var entity = beanMapper.toScheduledTask(taskId);
        scheduledTaskService.execute(entity);
        response.status(NO_CONTENT_204).send();
    }

    String taskId(ServerRequest request) {
        return pathArg(request, "task_id").get();
    }
}
