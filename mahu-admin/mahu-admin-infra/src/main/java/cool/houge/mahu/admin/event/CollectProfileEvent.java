package cool.houge.mahu.admin.event;

import cool.houge.mahu.model.result.AdminProfileResult;

/// 收集个人信息事件
///
/// 通过修改 [#profile] 的属性设置信息
///
/// @param uid 用户 ID
/// @param profile 个人信息
///
/// @author ZY (kzou227@qq.com)
public record CollectProfileEvent(long uid, AdminProfileResult profile) {}
