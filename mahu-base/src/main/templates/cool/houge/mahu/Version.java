package cool.houge.mahu;

/// 提供版本信息的简易类
public final class Version {

    private Version() {
        // cannot be instanciated
    }

    /**
     * Version.
     */
    public static final String VERSION = "@projectVersion@";

    /**
     * Revision Number.
     */
    public static final String REVISION = "@gitRevision@";

    /**
     * Build Timestamp.
     */
    public static final String BUILD_TIME = "@buildTime@";
}
