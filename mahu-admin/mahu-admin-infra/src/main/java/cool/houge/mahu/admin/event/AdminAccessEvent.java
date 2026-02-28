package cool.houge.mahu.admin.event;

import cool.houge.mahu.entity.sys.AdminAccessLog;

/// 管理员访问日志
///
/// @author ZY (kzou227@qq.com)
public record AdminAccessEvent(AdminAccessLog log) {}
