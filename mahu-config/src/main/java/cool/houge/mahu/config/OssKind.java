package cool.houge.mahu.config;

import lombok.Getter;

/// 对象存储（OSS）文件类型枚举
///
/// 定义了 MinIO 中不同类型文件的存储位置、内容类型和访问权限。
/// `prefix` 为存储桶中的路径前缀，`contentType` 为 MIME 类型限制，
/// `open` 表示是否为公开访问（true=公开，false=私有）。
///
/// @author ZY (kzou227@qq.com)
@Getter
public enum OssKind {
    /// 管理员头像（公开）
    ADMIN_AVATAR("p/admin-avatars", true),
    /// 用户头像（公开）
    USER_AVATAR("p/user-avatars", true),
    /// PCS 资源（公开）
    PCS_ASSET("p/pcs-assets", true),
    /// PCS 评论（公开）
    PCS_COMMENT("p/pcs-comments", true),
    /// 用户隐私文件（私有）
    USER_PRIVACY("user-privacy", false),
    ;

    private static final String IMAGE_CONTENT_TYPE = "image/*";

    /// 存储桶中的路径前缀
    private final String prefix;
    /// MIME 类型限制
    private final String contentType;
    /// 是否为公开访问
    private final boolean open;

    OssKind(String prefix, boolean open) {
        this.prefix = prefix;
        this.contentType = IMAGE_CONTENT_TYPE;
        this.open = open;
    }

    /// 判断当前枚举是否与指定的名称匹配
    ///
    /// @param name 要比较的名称
    /// @return 如果匹配返回true，否则返回false
    public boolean matches(String name) {
        return this.name().equals(name);
    }
}
