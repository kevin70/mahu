package cool.houge.mahu.config;

/// OSS 存储类型
///
/// @author ZY (kzou227@qq.com)
public enum OssKind {
    /// 品牌
    BRAND("brands"),
    /// 管理员头像
    ADMIN_AVATAR("admin-avatars"),
    /// 商店资源
    SHOP_ASSET("shop-assets");

    private final String prefix;

    OssKind(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
