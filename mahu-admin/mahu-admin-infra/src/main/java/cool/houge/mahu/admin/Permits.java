package cool.houge.mahu.admin;

/// 权限定义
///
/// @author ZY (kzou227@qq.com)
public enum Permits {
    DICT("字典"),
    ROLE("角色"),
    EMPLOYEE("员工"),
    DEPARTMENT("部门"),
    CLIENT("终端"),
    ACCESS_LOG("访问日志"),
    AUDIT_JOUR("操作审计"),
    //
    BRAND("品牌"),
    ;
    private final String label;

    Permits(String label) {
        this.label = label;
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
}
