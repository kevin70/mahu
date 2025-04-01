package cool.houge.mahu.admin.internal;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.oas.model.GetAdminAccessLogResponse;
import cool.houge.mahu.admin.oas.model.GetAdminAuditLogResponse;

///
/// @author ZY (kzou227@qq.com)
public interface LogBeanMapper {

    GetAdminAccessLogResponse toGetAdminAccessLogResponse(AdminAccessLog bean);

    GetAdminAuditLogResponse toGetAdminAuditLogResponse(AdminAuditLog bean);
}
