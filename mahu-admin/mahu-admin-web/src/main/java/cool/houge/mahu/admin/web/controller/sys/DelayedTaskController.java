package cool.houge.mahu.admin.web.controller.sys;

import cool.houge.mahu.admin.mapping.SysBeanMapper;
import cool.houge.mahu.admin.oas.controller.HDelayedTaskService;
import cool.houge.mahu.admin.sys.service.DelayedTaskService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 延时任务
@Service.Singleton
@AllArgsConstructor
public class DelayedTaskController implements HDelayedTaskService, WebSupport {

    private final SysBeanMapper beanMapper;
    private final DelayedTaskService delayedTaskService;

    @Override
    public void pageSysDelayedTask(ServerRequest request, ServerResponse response) {
        var page = page(request);
        var topic = queryArg(request, "topic").map(String::trim).orElse(null);
        var plist = delayedTaskService.findPage(topic, page);
        var rs = beanMapper.toPageResponse(plist, beanMapper::toSysDelayedTaskResponse);
        response.send(rs);
    }
}
