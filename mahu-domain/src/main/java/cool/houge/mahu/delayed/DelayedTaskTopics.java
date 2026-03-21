package cool.houge.mahu.delayed;

/**
 * sys.delayed_tasks.topic 的统一口径。
 *
 * 落库值采用 {@link Enum#name()}，以保证跨模块（admin 写入、worker 领取）一致。
 */
public enum DelayedTaskTopics {
    FEATURE_FLAG_ENABLE(3, 60, 0),
    FEATURE_FLAG_DISABLE(3, 60, 0);

    private final int maxAttempts;
    private final int leaseSeconds;
    /// sys.delayed_tasks.feature_id（用于追踪统计，不参与 worker/handler 业务定位）
    private final int featureFlagId;

    DelayedTaskTopics(int maxAttempts, int leaseSeconds, int featureFlagId) {
        this.maxAttempts = maxAttempts;
        this.leaseSeconds = leaseSeconds;
        this.featureFlagId = featureFlagId;
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

    public int featureFlagId() {
        return featureFlagId;
    }
}
