package cool.houge.mahu.config;

import lombok.Getter;

/// OSS 存储类型
///
/// @author ZY (kzou227@qq.com)
@Getter
@SuppressWarnings({"java:S1192"})
public enum OssKind {
    /// 管理员头像
    ADMIN_AVATAR("p/admin-avatars", "image/*", true),

    /// 用户头像
    USER_AVATAR("p/user-avatars", "image/*", true),

    /// 陪诊静态资源
    PCS_ASSET("p/pcs-assets", "image/*", true),

    /// 陪诊系统评论资源
    PCS_COMMENT("p/pcs-comments", "image/*", true),

    /// 用户隐私文件
    USER_PRIVACY("user-privacy", "image/*", false),
//
;

    /// 存储对象的目录
    private final String prefix;
    /// 存储类型
    private final String contentType;
    /// 开放访问的
    private final boolean open;

    OssKind(String prefix, String contentType, boolean open) {
        this.prefix = prefix;
        this.contentType = contentType;
        this.open = open;
    }

    /// 匹配指定名称
    public boolean matches(String name) {
        return this.name().equals(name);
    }
}
