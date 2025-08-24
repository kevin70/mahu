package cool.houge.mahu.admin.internal;

import cool.houge.mahu.admin.bean.Profile;
import cool.houge.mahu.admin.oas.model.AdminResponse;
import cool.houge.mahu.admin.oas.model.ClientResponse;
import cool.houge.mahu.admin.oas.model.DictResponse;
import cool.houge.mahu.admin.oas.model.LoginRequest;
import cool.houge.mahu.admin.oas.model.MeProfileResponse;
import cool.houge.mahu.admin.oas.model.PublicDictDataResponse;
import cool.houge.mahu.admin.oas.model.PublicDictTypeResponse;
import cool.houge.mahu.admin.oas.model.RoleResponse;
import cool.houge.mahu.admin.oas.model.ScheduledTaskExecutionResponse;
import cool.houge.mahu.admin.oas.model.ScheduledTaskResponse;
import cool.houge.mahu.admin.oas.model.TokenPasswordForm;
import cool.houge.mahu.admin.oas.model.TokenRefreshTokenForm;
import cool.houge.mahu.admin.oas.model.TokenResponse;
import cool.houge.mahu.admin.oas.model.UpdateMePasswordRequest;
import cool.houge.mahu.admin.oas.model.UpdateMeProfileRequest;
import cool.houge.mahu.admin.oas.model.UpsertAdminRequest;
import cool.houge.mahu.admin.oas.model.UpsertClientRequest;
import cool.houge.mahu.admin.oas.model.UpsertDictRequest;
import cool.houge.mahu.admin.oas.model.UpsertRoleRequest;
import cool.houge.mahu.admin.system.dto.TokenPayload;
import cool.houge.mahu.admin.system.dto.TokenResult;
import cool.houge.mahu.entity.log.ScheduledExecutionLog;
import cool.houge.mahu.entity.system.Admin;
import cool.houge.mahu.entity.system.Client;
import cool.houge.mahu.entity.system.Dict;
import cool.houge.mahu.entity.system.DictType;
import cool.houge.mahu.entity.system.Role;
import cool.houge.mahu.entity.system.ScheduledTask;
import cool.houge.mahu.util.GrantType;
import java.util.List;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/// 系统模块对象映射
///
/// @author ZY (kzou227@qq.com)
public interface SystemBeanMapper {

    DictType toDictType(UpsertDictRequest bean);

    DictResponse toDictResponse(DictType bean);

    @Mapping(target = "data", conditionExpression = "java(includeData)")
    PublicDictTypeResponse toPublicDictTypeResponse(DictType bean, boolean includeData);

    PublicDictDataResponse toPublicDictDataResponse(Dict bean);

    TokenPasswordForm toTokenPasswordForm(LoginRequest bean);

    TokenRefreshTokenForm toTokenRefreshTokenForm(LoginRequest bean);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenPasswordForm bean, GrantType type);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenRefreshTokenForm bean, GrantType type);

    TokenResponse toTokenResponse(TokenResult bean);

    Admin toAdmin(UpdateMePasswordRequest bean);

    Admin toAdmin(UpdateMeProfileRequest bean);

    MeProfileResponse toGetMeProfileResponse(Profile bean);

    Client toClient(UpsertClientRequest bean);

    ClientResponse toClientResponse(Client bean);

    Role toRole(UpsertRoleRequest bean);

    RoleResponse toRoleResponse(Role bean);

    @Named("roleIdsToRoles")
    default List<Role> roleIdsToRoles(List<Integer> roleIds) {
        if (roleIds == null) return null;
        return roleIds.stream().map(id -> new Role().setId(id)).toList();
    }

    @Named("rolesToRoleIds")
    default List<Integer> rolesToRoleIds(List<Role> roles) {
        if (roles == null) return null;
        return roles.stream().map(Role::getId).toList();
    }

    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "roleIdsToRoles")
    Admin toAdmin(UpsertAdminRequest bean);

    @Mapping(target = "roleIds", source = "roles", qualifiedByName = "rolesToRoleIds")
    AdminResponse toAdminResponse(Admin bean);

    @Mapping(target = "taskName", source = "taskId.taskName")
    @Mapping(target = "taskInstance", source = "taskId.taskInstance")
    ScheduledTaskResponse toScheduledTaskResponse(ScheduledTask bean);

    @Mapping(target = "taskName", source = "scheduledTask.taskId.taskName")
    @Mapping(target = "taskInstance", source = "scheduledTask.taskId.taskInstance")
    ScheduledTaskExecutionResponse toScheduledTaskExecutionResponse(ScheduledExecutionLog bean);

    @Mapping(target = "taskId.taskName", source = "taskName")
    @Mapping(target = "taskId.taskInstance", source = "taskInstance")
    ScheduledTask toScheduledTask(String taskName, String taskInstance);
}
