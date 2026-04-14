package cool.houge.mahu.admin.event;

import cool.houge.mahu.entity.sys.AdminLoginAttempt;

/// 管理员登录尝试记录
///
/// @author ZY (kzou227@qq.com)
public record AdminLoginAttemptEvent(AdminLoginAttempt log) {}
