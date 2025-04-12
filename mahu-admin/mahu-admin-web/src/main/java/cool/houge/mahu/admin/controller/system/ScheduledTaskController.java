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

import static cool.houge.mahu.admin.Permits.SYSTEM_SCHEDULED_TASK;

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
        rules.get("/system/scheduled-tasks", authz(SYSTEM_SCHEDULED_TASK.R()).wrap(this::listSystemScheduledTasks));
        rules.get(
                "/system/scheduled-tasks/{task_name}/{task_instance}/executions",
                authz(SYSTEM_SCHEDULED_TASK.R()).wrap(this::listSystemScheduledTaskExecutions));
        rules.post(
                "/system/scheduled-tasks/{task_name}/{task_instance}/executions",
                authz(SYSTEM_SCHEDULED_TASK.W()).wrap(this::executeSystemScheduledTask));
        rules.delete(
                "/system/scheduled-tasks/{task_name}/{task_instance}/executions",
                authz(SYSTEM_SCHEDULED_TASK.W()).wrap(this::deleteSystemScheduledTaskExecution));
    }

    private void listSystemScheduledTasks(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = scheduledTaskService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(
                plist.getList(), plist.getTotalCount(), beanMapper::toGetSystemScheduledTaskResponse);
        response.send(rs);
    }

    private void listSystemScheduledTaskExecutions(ServerRequest request, ServerResponse response) {
        //
    }

    private void executeSystemScheduledTask(ServerRequest request, ServerResponse response) {
        //
    }

    private void deleteSystemScheduledTaskExecution(ServerRequest request, ServerResponse response) {
        //
    }
}
