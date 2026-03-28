package cool.houge.mahu.config;

/// 延时任务主题定义
///
/// 定义了系统中所有支持的延时任务主题及其重试策略。
/// 任务主题落库值采用 {@link Enum#name()}，以保证跨模块一致（admin 写入、worker 领取）。
///
/// @author ZY (kzou227@qq.com)
public enum DelayedTaskTopic {
    /// 功能开关启用任务
    FEATURE_FLAG_ENABLE(3, 60),
    /// 功能开关禁用任务
    FEATURE_FLAG_DISABLE(3, 60);

    /// 最大重试次数
    private final int maxAttempts;
    /// 租赁锁定时间（秒）
    private final int leaseSeconds;

    DelayedTaskTopic(int maxAttempts, int leaseSeconds) {
        this.maxAttempts = maxAttempts;
        this.leaseSeconds = leaseSeconds;
    }

    /// 获取任务主题名称
    /// 返回值为 enum.name()，确保与数据库存储值一致
    ///
    /// @return 任务主题名称
    public String topic() {
        return name();
    }

    /// 获取最大重试次数
    ///
    /// @return 最大重试次数
    public int maxAttempts() {
        return maxAttempts;
    }

    /// 获取任务租赁锁定时间
    ///
    /// @return 锁定时间（秒）
    public int leaseSeconds() {
        return leaseSeconds;
    }
}
