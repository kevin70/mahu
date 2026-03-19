package cool.houge.mahu.admin.mapping;

import cool.houge.mahu.admin.oas.vo.FileCreatePresignedRequest;
import cool.houge.mahu.admin.oas.vo.FileCreatePresignedResponse;
import cool.houge.mahu.admin.oas.vo.FileType;
import cool.houge.mahu.admin.oas.vo.IdPhotoCreatePresignedRequest;
import cool.houge.mahu.admin.oas.vo.IdPhotoCreatePresignedResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminAccessLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminAuthLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminChangeLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysAuthClientResponse;
import cool.houge.mahu.admin.oas.vo.SysAuthClientUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysDelayedTaskResponse;
import cool.houge.mahu.admin.oas.vo.SysDictGroupResponse;
import cool.houge.mahu.admin.oas.vo.SysDictGroupUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysDictUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysFeatureFlagCreateRequest;
import cool.houge.mahu.admin.oas.vo.SysFeatureFlagResponse;
import cool.houge.mahu.admin.oas.vo.SysFeatureFlagUpdateRequest;
import cool.houge.mahu.admin.oas.vo.SysRoleResponse;
import cool.houge.mahu.admin.oas.vo.SysRoleUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysScheduledTaskLogResponse;
import cool.houge.mahu.admin.oas.vo.SysScheduledTaskResponse;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.entity.sys.AdminAccessLog;
import cool.houge.mahu.entity.sys.AdminAuthLog;
import cool.houge.mahu.entity.sys.AdminChangeLog;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.entity.sys.Role;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.entity.sys.ScheduledTaskLog;
import cool.houge.mahu.entity.sys.StoredObject;
import cool.houge.mahu.shared.dto.PresignedUploadPayload;
import cool.houge.mahu.shared.dto.PresignedUploadResult;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.time.OffsetDateTime;
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

    default Instant map(OffsetDateTime v) {
        return v == null ? null : v.toInstant();
    }

    default OffsetDateTime map(Instant v) {
        return v == null ? null : v.atOffset(java.time.ZoneOffset.UTC);
    }

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

    DictGroup toDictGroup(SysDictGroupUpsertRequest bean);

    Dict toDict(SysDictUpsertRequest bean);

    SysDictGroupResponse toSysDictGroupResponse(DictGroup bean);

    AuthClient toAuthClient(SysAuthClientUpsertRequest bean);

    SysAuthClientResponse toSysAuthClientResponse(AuthClient bean);

    Role toRole(SysRoleUpsertRequest bean);

    SysRoleResponse toSysRoleResponse(Role bean);

    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "roleIdsToRoles")
    Admin toAdmin(SysAdminUpsertRequest bean);

    @Mapping(target = "roleIds", source = "roles", qualifiedByName = "rolesToRoleIds")
    SysAdminResponse toSysAdminResponse(Admin bean);

    @Mapping(target = "taskData", source = "taskDataBase64")
    SysScheduledTaskResponse toSysScheduledTaskResponse(ScheduledTask bean);

    SysDelayedTaskResponse toSysDelayedTaskResponse(DelayedTask bean);

    @Mapping(target = "taskName", source = "scheduledTask.taskName")
    SysScheduledTaskLogResponse toSysScheduledTaskLogResponse(ScheduledTaskLog bean);

    ScheduledTask toScheduledTask(String taskName);

    SysFeatureFlagResponse toSysFeatureFlagResponse(FeatureFlag bean);

    SysAdminAuthLogResponse toAdminAuthLogResponse(AdminAuthLog bean);

    SysAdminAccessLogResponse toAdminAccessLogResponse(AdminAccessLog bean);

    SysAdminChangeLogResponse toAdminChangeLogResponse(AdminChangeLog bean);

    PresignedUploadPayload toPresignedUploadPayload(FileCreatePresignedRequest bean);

    StoredObject.Type toStoredObjectType(FileType bean);

    FeatureFlag toFeatureFlag(SysFeatureFlagCreateRequest bean);

    FeatureFlag toFeatureFlag(SysFeatureFlagUpdateRequest bean);

    FileCreatePresignedResponse toFileCreatePresignedResponse(PresignedUploadResult bean);

    PresignedUploadPayload toPresignedUploadPayload(IdPhotoCreatePresignedRequest bean);

    IdPhotoCreatePresignedResponse toIdPhotoCreatePresignedResponse(PresignedUploadResult bean);
}
