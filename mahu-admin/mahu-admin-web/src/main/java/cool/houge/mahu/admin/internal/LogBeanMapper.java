package cool.houge.mahu.admin.internal;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.AdminAuthLog;
import cool.houge.mahu.admin.oas.model.GetAdminAccessLogResponse;
import cool.houge.mahu.admin.oas.model.GetAdminAuditLogResponse;
import cool.houge.mahu.admin.oas.model.GetAdminAuthLogResponse;

/// 日志对象映射
///
/// @author ZY (kzou227@qq.com)
public interface LogBeanMapper {

    GetAdminAuthLogResponse toGetAdminAuthLogResponse(AdminAuthLog bean);

    GetAdminAccessLogResponse toGetAdminAccessLogResponse(AdminAccessLog bean);

    GetAdminAuditLogResponse toGetAdminAuditLogResponse(AdminAuditLog bean);
}
