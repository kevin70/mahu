package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.internal.TopBeanMapper;
import cool.houge.mahu.admin.oas.vo.FileCreatePresignedRequest;
import cool.houge.mahu.admin.oas.vo.FileCreatePresignedResponse;
import cool.houge.mahu.admin.oas.vo.IdPhotoCreatePresignedRequest;
import cool.houge.mahu.admin.oas.vo.IdPhotoCreatePresignedResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminAccessLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminAuthLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminChangeLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysAuthClientResponse;
import cool.houge.mahu.admin.oas.vo.SysAuthClientUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysDictTypeResponse;
import cool.houge.mahu.admin.oas.vo.SysDictTypeUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysDictUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysFeatureResponse;
import cool.houge.mahu.admin.oas.vo.SysFeatureUpdateRequest;
import cool.houge.mahu.admin.oas.vo.SysRoleResponse;
import cool.houge.mahu.admin.oas.vo.SysRoleUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysScheduledTaskExeResponse;
import cool.houge.mahu.admin.oas.vo.SysScheduledTaskResponse;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.entity.sys.AdminAccessLog;
import cool.houge.mahu.entity.sys.AdminAuthLog;
import cool.houge.mahu.entity.sys.AdminChangeLog;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.entity.sys.Feature;
import cool.houge.mahu.entity.sys.Role;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.entity.sys.ScheduledTaskLog;
import cool.houge.mahu.shared.dto.PresignedUploadPayload;
import cool.houge.mahu.shared.dto.PresignedUploadResult;
import io.helidon.service.registry.Service;
import java.util.List;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/// 系统模块对象映射
///
/// @author ZY (kzou227@qq.com)
@AnnotateWith(Service.Singleton.class)
@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SysBeanMapper extends TopBeanMapper {

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

    DictGroup toDictType(SysDictTypeUpsertRequest bean);

    Dict toDict(SysDictUpsertRequest bean);

    SysDictTypeResponse toSysDictTypeResponse(DictGroup bean);

    AuthClient toAuthClient(SysAuthClientUpsertRequest bean);

    SysAuthClientResponse toSysAuthClientResponse(AuthClient bean);

    Role toRole(SysRoleUpsertRequest bean);

    SysRoleResponse toSysRoleResponse(Role bean);

    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "roleIdsToRoles")
    Admin toAdmin(SysAdminUpsertRequest bean);

    @Mapping(target = "roleIds", source = "roles", qualifiedByName = "rolesToRoleIds")
    SysAdminResponse toSysAdminResponse(Admin bean);

    SysScheduledTaskResponse toSysScheduledTaskResponse(ScheduledTask bean);

    @Mapping(target = "taskName", source = "scheduledTask.taskName")
    SysScheduledTaskExeResponse toSysScheduledTaskExeResponse(ScheduledTaskLog bean);

    ScheduledTask toScheduledTask(String taskName);

    SysFeatureResponse toSysFeatureResponse(Feature bean);

    SysAdminAuthLogResponse toAdminAuthLogResponse(AdminAuthLog bean);

    SysAdminAccessLogResponse toAdminAccessLogResponse(AdminAccessLog bean);

    SysAdminChangeLogResponse toAdminChangeLogResponse(AdminChangeLog bean);

    Feature toFeature(SysFeatureUpdateRequest bean);

    PresignedUploadPayload toPresignedUploadPayload(FileCreatePresignedRequest bean);

    FileCreatePresignedResponse toFileCreatePresignedResponse(PresignedUploadResult bean);

    PresignedUploadPayload toPresignedUploadPayload(IdPhotoCreatePresignedRequest bean);

    IdPhotoCreatePresignedResponse toIdPhotoCreatePresignedResponse(PresignedUploadResult bean);
}
