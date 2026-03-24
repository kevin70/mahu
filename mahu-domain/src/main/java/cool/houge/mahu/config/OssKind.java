package cool.houge.mahu.config;

import lombok.Getter;

@Getter
@SuppressWarnings({"java:S1192"})
public enum OssKind {
    ADMIN_AVATAR("p/admin-avatars", "image/*", true),
    USER_AVATAR("p/user-avatars", "image/*", true),
    PCS_ASSET("p/pcs-assets", "image/*", true),
    PCS_COMMENT("p/pcs-comments", "image/*", true),
    USER_PRIVACY("user-privacy", "image/*", false),
    ;

    private final String prefix;
    private final String contentType;
    private final boolean open;

    OssKind(String prefix, String contentType, boolean open) {
        this.prefix = prefix;
        this.contentType = contentType;
        this.open = open;
    }

    public boolean matches(String name) {
        return this.name().equals(name);
    }
}
