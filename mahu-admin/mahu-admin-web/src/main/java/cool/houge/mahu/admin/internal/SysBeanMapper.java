package cool.houge.mahu.admin.internal;

import cool.houge.mahu.admin.bean.Profile;
import cool.houge.mahu.admin.entity.Admin;
import cool.houge.mahu.admin.entity.Role;
import cool.houge.mahu.admin.oas.vo.LoginTokenRequest;
import cool.houge.mahu.admin.oas.vo.LoginTokenResponse;
import cool.houge.mahu.admin.oas.vo.MePasswordUpdateRequest;
import cool.houge.mahu.admin.oas.vo.MeProfileResponse;
import cool.houge.mahu.admin.oas.vo.MeProfileUpdateRequest;
import cool.houge.mahu.admin.oas.vo.PublicDictResponse;
import cool.houge.mahu.admin.oas.vo.PublicDictTypeResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysAuthClientResponse;
import cool.houge.mahu.admin.oas.vo.SysAuthClientUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysDictTypeResponse;
import cool.houge.mahu.admin.oas.vo.SysDictTypeUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysDictUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysFeatureResponse;
import cool.houge.mahu.admin.oas.vo.SysRoleResponse;
import cool.houge.mahu.admin.oas.vo.SysRoleUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysScheduledTaskExeResponse;
import cool.houge.mahu.admin.oas.vo.SysScheduledTaskResponse;
import cool.houge.mahu.admin.oas.vo.TokenPasswordForm;
import cool.houge.mahu.admin.oas.vo.TokenRefreshTokenForm;
import cool.houge.mahu.admin.sys.dto.TokenPayload;
import cool.houge.mahu.admin.sys.dto.TokenResult;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictType;
import cool.houge.mahu.entity.sys.Feature;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.entity.sys.ScheduledTaskExeLog;
import cool.houge.mahu.util.GrantType;
import java.util.List;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/// 系统模块对象映射
///
/// @author ZY (kzou227@qq.com)
public interface SysBeanMapper {

    DictType toDictType(SysDictTypeUpsertRequest bean);

    Dict toDict(SysDictUpsertRequest bean);

    SysDictTypeResponse toSysDictTypeResponse(DictType bean);

    @Mapping(target = "data", conditionExpression = "java(includeData)")
    PublicDictTypeResponse toPublicDictTypeResponse(DictType bean, boolean includeData);

    PublicDictResponse toPublicDictDataResponse(Dict bean);

    TokenPasswordForm toTokenPasswordForm(LoginTokenRequest bean);

    TokenRefreshTokenForm toTokenRefreshTokenForm(LoginTokenRequest bean);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenPasswordForm bean, GrantType type);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenRefreshTokenForm bean, GrantType type);

    LoginTokenResponse toLoginTokenResponse(TokenResult bean);

    Admin toAdmin(MePasswordUpdateRequest bean);

    Admin toAdmin(MeProfileUpdateRequest bean);

    MeProfileResponse toMeProfileResponse(Profile bean);

    AuthClient toAuthClient(SysAuthClientUpsertRequest bean);

    SysAuthClientResponse toSysAuthClientResponse(AuthClient bean);

    Role toRole(SysRoleUpsertRequest bean);

    SysRoleResponse toSysRoleResponse(Role bean);

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
    Admin toAdmin(SysAdminUpsertRequest bean);

    @Mapping(target = "roleIds", source = "roles", qualifiedByName = "rolesToRoleIds")
    SysAdminResponse toSysAdminResponse(Admin bean);

    SysScheduledTaskResponse toSysScheduledTaskResponse(ScheduledTask bean);

    @Mapping(target = "taskName", source = "scheduledTask.taskName")
    SysScheduledTaskExeResponse toSysScheduledTaskExeResponse(ScheduledTaskExeLog bean);

    ScheduledTask toScheduledTask(String taskName);

    SysFeatureResponse toSysFeatureResponse(Feature bean);
}
