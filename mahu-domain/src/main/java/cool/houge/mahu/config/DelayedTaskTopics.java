package cool.houge.mahu.config;

/**
 * sys.delayed_tasks.topic 的统一口径。
 *
 * 落库值采用 {@link Enum#name()}，以保证跨模块（admin 写入、worker 领取）一致。
 */
public enum DelayedTaskTopics {
    FEATURE_FLAG_ENABLE(3, 60),
    FEATURE_FLAG_DISABLE(3, 60);

    private final int maxAttempts;
    private final int leaseSeconds;

    DelayedTaskTopics(int maxAttempts, int leaseSeconds) {
        this.maxAttempts = maxAttempts;
        this.leaseSeconds = leaseSeconds;
    }

    public String topic() {
        // 明确表达：落库值 = enum.name()
        return name();
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public int leaseSeconds() {
        return leaseSeconds;
    }
}
