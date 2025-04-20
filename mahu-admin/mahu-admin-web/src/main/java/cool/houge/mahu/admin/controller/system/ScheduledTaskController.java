package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.system.service.ScheduledTaskService;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.SCHEDULED_TASK;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledTaskController implements WebSupport, HttpService {

    @Inject
    VoBeanMapper beanMapper;

    @Inject
    ScheduledTaskService scheduledTaskService;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/scheduled-tasks", authz(SCHEDULED_TASK.R()).wrap(this::listScheduledTasks));
        rules.get(
                "/scheduled-tasks/{task_name}/{task_instance}/executions",
                authz(SCHEDULED_TASK.R()).wrap(this::listScheduledTaskExecutions));
        rules.post(
                "/scheduled-tasks/{task_name}/{task_instance}/executions",
                authz(SCHEDULED_TASK.W()).wrap(this::executeScheduledTask));
        rules.delete(
                "/scheduled-tasks/{task_name}/{task_instance}/executions",
                authz(SCHEDULED_TASK.W()).wrap(this::deleteScheduledTaskExecution));
    }

    private void listScheduledTasks(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = scheduledTaskService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toScheduledTaskResponse);
        response.send(rs);
    }

    private void listScheduledTaskExecutions(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var taskName = pathParams.get("task_name");
        var taskInstance = pathParams.get("task_instance");

        var dataFilter = dataFilter(request);
        var plist = scheduledTaskService.findPage4ExecutionLog(taskName, taskInstance, dataFilter);
        var rs = beanMapper.toPageResponse(
                plist.getList(), plist.getTotalCount(), beanMapper::toScheduledTaskExecutionResponse);
        response.send(rs);
    }

    private void executeScheduledTask(ServerRequest request, ServerResponse response) {
        //
    }

    private void deleteScheduledTaskExecution(ServerRequest request, ServerResponse response) {
        //
    }
}
