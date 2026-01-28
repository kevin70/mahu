package cool.houge.mahu.admin.controller.sys;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.oas.controller.HAuthClientService;
import cool.houge.mahu.admin.oas.vo.SysAuthClientUpsertRequest;
import cool.houge.mahu.admin.sys.service.AuthClientService;
import cool.houge.mahu.entity.TerminalType;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.shared.query.AuthClientQuery;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class AuthClientController implements HAuthClientService, WebSupport {

    private final SysBeanMapper beanMapper;
    private final AuthClientService authClientService;

    @Override
    public void createSysAuthClient(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysAuthClientUpsertRequest.class);
        validate(vo);

        var entity = beanMapper.toAuthClient(vo);
        authClientService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void deleteSysAuthClient(ServerRequest request, ServerResponse response) {
        var clientId = clientId(request);

        authClientService.delete(new AuthClient().setClientId(clientId));
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void getSysAuthClient(ServerRequest request, ServerResponse response) {
        var clientId = clientId(request);

        var bean = authClientService.findById(clientId);
        response.send(beanMapper.toSysAuthClientResponse(bean));
    }

    @Override
    public void pageSysAuthClient(ServerRequest request, ServerResponse response) {
        var qb = AuthClientQuery.builder();
        queryArg(request, "client_id").ifPresent(qb::clientId);
        queryArg(request, "terminal_type").as(TerminalType::valueOf).ifPresent(qb::terminalType);
        queryArg(request, "wechat_appid").ifPresent(qb::wechatAppid);

        var plist = authClientService.findPage(qb.build(), page(request));
        var rs = beanMapper.toPageResponse(plist, beanMapper::toSysAuthClientResponse);
        response.send(rs);
    }

    @Override
    public void updateSysAuthClient(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysAuthClientUpsertRequest.class);
        validate(vo);

        var clientId = clientId(request);
        var entity = beanMapper.toAuthClient(vo).setClientId(clientId);
        authClientService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    String clientId(ServerRequest request) {
        return pathString(request, "client_id");
    }
}
