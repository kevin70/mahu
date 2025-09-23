package cool.houge.mahu.admin;

/// 权限定义
///
/// @author ZY (kzou227@qq.com)
public enum Permissions {
    // 系统模块
    SYS_AUTH_CLIENT(Var.A, "系统模块", "认证终端"),
    SYS_DICT(Var.A, "系统模块", "字典"),
    SYS_ROLE(Var.A, "系统模块", "角色"),
    SYS_ADMIN(Var.A, "系统模块", "管理员"),
    SYS_ADMIN_LOG(Var.R, "系统模块", "管理员日志"),
    SYS_SCHEDULED_TASK(Var.B, "系统模块", "定时任务"),

//
;

    private final int fnCodes;
    private final String module;
    private final String label;

    Permissions(int fnCodes, String module, String label) {
        this.fnCodes = fnCodes;
        this.module = module;
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

    /// 权限模块
    public String module() {
        return module;
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
