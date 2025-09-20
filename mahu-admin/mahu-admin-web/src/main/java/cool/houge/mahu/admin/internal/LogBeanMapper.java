package cool.houge.mahu.admin.internal;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.AdminAuthLog;
import cool.houge.mahu.admin.oas.vo.SysAdminAccessLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminAuditLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminAuthLogResponse;

/// 日志对象映射
///
/// @author ZY (kzou227@qq.com)
public interface LogBeanMapper {

    SysAdminAuthLogResponse toAdminAuthLogResponse(AdminAuthLog bean);

    SysAdminAccessLogResponse toAdminAccessLogResponse(AdminAccessLog bean);

    SysAdminAuditLogResponse toAdminAuditLogResponse(AdminAuditLog bean);
}
