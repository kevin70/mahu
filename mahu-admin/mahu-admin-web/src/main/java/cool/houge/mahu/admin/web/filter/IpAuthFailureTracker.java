package cool.houge.mahu.admin.web.filter;

import java.time.Instant;
import java.util.Optional;

/// 认证失败跟踪器（按 IP 维度）。
///
/// 设计为接口，便于未来替换为 Redis 等共享存储实现。
public interface IpAuthFailureTracker {

    /// 返回当前封禁截止时间（若未封禁则为空）。
    Optional<Instant> blockedUntil(String clientIp, Instant now);

    /// 认证失败计数 +1，返回累计失败次数。
    int incrementFailure(String clientIp);

    /// 设置封禁截止时间。
    void block(String clientIp, Instant blockedUntil);

    /// 清理失败与封禁状态（用于认证成功）。
    void clear(String clientIp);
}
