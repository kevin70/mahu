package cool.houge.mahu.admin.web.controller;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.mapping.VoBeanMapper;
import cool.houge.mahu.admin.oas.controller.HMeService;
import cool.houge.mahu.admin.oas.vo.MeNotificationReadBatchRequest;
import cool.houge.mahu.admin.oas.vo.MePasswordUpdateRequest;
import cool.houge.mahu.admin.oas.vo.MeProfileUpdateRequest;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.sys.service.AdminService;
import cool.houge.mahu.admin.sys.service.MeNotificationService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 个人信息
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class MeController implements HMeService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final AdminService adminService;
    private final MeNotificationService meNotificationService;

    @Override
    public void getMeProfile(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.current();
        var dto = adminService.getProfile(ac.adminId());
        dto.setPermissionCodes(ac.permissions());

        var rs = beanMapper.toMeProfileResponse(dto);
        response.send(rs);
    }

    @Override
    public void updateMeProfile(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(MeProfileUpdateRequest.class);
        validate(vo);

        var ac = AuthContext.current();
        var user = beanMapper.toAdmin(vo).setId(ac.adminId());
        adminService.update(user);

        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void updateMePassword(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(MePasswordUpdateRequest.class);
        validate(vo);

        var ac = AuthContext.current();
        var entity = beanMapper.toAdmin(vo).setId(ac.adminId());
        adminService.updatePassword(entity);

        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void pageMeNotification(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.current();
        var page = page(request);
        var read = queryBoolean(request, "read").orElse(null);

        var plist = meNotificationService.pageViews(page, ac.adminId(), read);
        var rs = beanMapper.toPageResponse(plist, beanMapper::toMeNotificationResponse);
        response.send(rs);
    }

    @Override
    public void getMeNotificationPoll(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.current();
        var cursor = queryArg(request, "cursor").orElse("");
        var limit = queryInt(request, "limit").orElse(50);
        var includeRead = queryBoolean(request, "include_read").orElse(false);

        var result = meNotificationService.poll(ac.adminId(), cursor, limit, includeRead);
        var rs = beanMapper.toMeNotificationPollResponse(result);
        response.send(rs);
    }

    @Override
    public void updateMeNotificationRead(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.current();
        var notificationId = pathLong(request, "notification_id");
        meNotificationService.markRead(ac.adminId(), notificationId);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void updateBatchMeNotificationRead(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.current();
        var vo = request.content().as(MeNotificationReadBatchRequest.class);
        validate(vo);
        meNotificationService.markReadBatch(ac.adminId(), vo.getNotificationIds());
        response.status(NO_CONTENT_204).send();
    }

}
