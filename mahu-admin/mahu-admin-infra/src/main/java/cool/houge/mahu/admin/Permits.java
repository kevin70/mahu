package cool.houge.mahu.admin;

import static cool.houge.mahu.admin.Permits.Constants.A;
import static cool.houge.mahu.admin.Permits.Constants.R;

/// 权限定义
///
/// @author ZY (kzou227@qq.com)
public enum Permits {
    DICT(A, "字典"),
    ROLE(A, "角色"),
    ADMIN(A, "管理员"),
    DEPARTMENT(A, "部门"),
    CLIENT(A, "终端"),
    ACCESS_LOG(R, "访问日志"),
    AUDIT_LOG(R, "操作审计"),
    SCHEDULED_TASK(A, "系统|定时任务"),
    //
    BRAND(A, "品牌"),
    // 商城
    MART_SHOP(A, "商城-商店"),
    MART_CATEGORY(A, "商城-分类"),
    MART_ASSET(A, "商城-资源"),
    MART_ATTRIBUTE(A, "商城|产品属性"),
    MART_PRODUCT(A, "商城|产品"),
    // 业务日志
    ADMIN_ACCESS_LOG(R, "管理员|访问日志"),
    ADMIN_AUDIT_LOG(R, "管理员|操作日志"),
    ADMIN_AUTH_LOG(R, "管理员|认证日志"),

//
;

    private final int fnCodes;
    private final String label;

    Permits(int fnCodes, String label) {
        this.fnCodes = fnCodes;
        this.label = label;
    }

    /// 是否能读取
    public boolean canRead() {
        return (fnCodes & 1) == 1;
    }

    /// 是否能写入
    public boolean canWrite() {
        return (fnCodes >> 1 & 1) == 1;
    }

    /// 是否能删除
    public boolean canDelete() {
        return (fnCodes >> 2 & 1) == 1;
    }

    /// 读取权限代码
    public String R() {
        return name() + ":R";
    }

    /// 写入权限代码
    public String W() {
        return name() + ":W";
    }

    /// 删除权限代码
    public String D() {
        return name() + ":D";
    }

    /// 权限文本
    public String label() {
        return label;
    }

    static class Constants {
        /// 读取功能
        static final int R = 1;
        /// 写入功能
        static final int W = 1 << 1;
        /// 删除功能
        static final int D = 1 << 2;
        /// 支持所有功能
        static final int A = R | W | D;
    }
}
