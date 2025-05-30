package cool.houge.mahu.admin;

/// 权限定义
///
/// @author ZY (kzou227@qq.com)
public enum Permits {
    // 系统相关
    DICT(Var.A, "系统|字典管理"),
    ROLE(Var.A, "系统|角色管理"),
    ADMIN(Var.A, "系统|管理员"),
    DEPARTMENT(Var.A, "系统|部门管理"),
    CLIENT(Var.A, "系统|认证终端"),
    SCHEDULED_TASK(Var.B, "系统|定时任务"),

    // 基础
    BRAND(Var.A, "基础|品牌管理"),

    // 商城
    MART_SHOP(Var.A, "商城|商店"),
    MART_CATEGORY(Var.A, "商城|分类"),
    MART_ASSET(Var.A, "商城|资源"),
    MART_ATTRIBUTE(Var.A, "商城|产品属性"),
    MART_PRODUCT(Var.A, "商城|产品"),

    // 业务日志
    ADMIN_ACCESS_LOG(Var.R, "管理员|访问日志"),
    ADMIN_AUDIT_LOG(Var.R, "管理员|操作日志"),
    ADMIN_AUTH_LOG(Var.R, "管理员|认证日志"),

//
;

    private final int fnCodes;
    private final String label;

    /// 读取权限代码
    @SuppressWarnings("java:S116")
    public final String R;

    /// 写入权限代码
    @SuppressWarnings("java:S116")
    public final String W;

    /// 删除权限代码
    @SuppressWarnings("java:S116")
    public final String D;

    Permits(int fnCodes, String label) {
        this.fnCodes = fnCodes;
        this.label = label;
        this.R = name() + ":R";
        this.W = name() + ":W";
        this.D = name() + ":D";
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

    /// 权限文本
    public String label() {
        return label;
    }

    private static class Var {
        /// 读取功能
        static final int R = 1;
        /// 写入功能
        static final int W = 1 << 1;
        /// 删除功能
        static final int D = 1 << 2;
        /// 支持所有功能
        static final int A = R | W | D;
        /// 支持读取写入
        static final int B = R | W;
    }
}
