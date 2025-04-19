package cool.houge.mahu.admin.internal;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.AdminAuthLog;
import cool.houge.mahu.admin.oas.model.AdminAccessLogResponse;
import cool.houge.mahu.admin.oas.model.AdminAuditLogResponse;
import cool.houge.mahu.admin.oas.model.AdminAuthLogResponse;

/// 日志对象映射
///
/// @author ZY (kzou227@qq.com)
public interface LogBeanMapper {

    AdminAuthLogResponse toAdminAuthLogResponse(AdminAuthLog bean);

    AdminAccessLogResponse toAdminAccessLogResponse(AdminAccessLog bean);

    AdminAuditLogResponse toAdminAuditLogResponse(AdminAuditLog bean);
}
