package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.oas.controller.HDelayedTaskService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 延时任务
@Service.Singleton
@AllArgsConstructor
public class DelayedTaskController implements HDelayedTaskService, WebSupport {

    @Override
    public void pageSysDelayedTask(ServerRequest request, ServerResponse response) {
        //
    }
}
