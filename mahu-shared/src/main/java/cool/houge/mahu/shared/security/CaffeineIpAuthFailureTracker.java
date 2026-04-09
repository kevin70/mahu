package cool.houge.mahu.shared.security;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/// 基于 Caffeine 的 IP 认证失败跟踪器（单实例内存）。
@Service.Singleton
public class CaffeineIpAuthFailureTracker implements IpAuthFailureTracker {

    private static final long MAX_ENTRIES = 100_000;
    private static final Duration FAILED_ATTEMPTS_TTL = Duration.ofMinutes(30);
    private final com.github.benmanes.caffeine.cache.Cache<String, Integer> failedAttemptsCache = Caffeine.newBuilder()
            .maximumSize(MAX_ENTRIES)
            // 连续失败统计仅需要短期保留，避免历史失败长期累积。
            .expireAfterWrite(FAILED_ATTEMPTS_TTL)
            .build();
    private final com.github.benmanes.caffeine.cache.Cache<String, Instant> blockedUntilCache = Caffeine.newBuilder()
            .maximumSize(MAX_ENTRIES)
            // 按 blockedUntil 动态过期，到达封禁截止时间后自动移除。
            .expireAfter(new BlockedUntilExpiry())
            .build();

    @Override
    public Optional<Instant> blockedUntil(String clientIp, Instant now) {
        var blockedUntil = blockedUntilCache.getIfPresent(clientIp);
        if (blockedUntil == null) {
            return Optional.empty();
        }
        if (blockedUntil.isAfter(now)) {
            return Optional.of(blockedUntil);
        }
        clear(clientIp);
        return Optional.empty();
    }

    @Override
    public int incrementFailure(String clientIp) {
        return failedAttemptsCache.asMap().merge(clientIp, 1, Integer::sum);
    }

    @Override
    public void block(String clientIp, Instant blockedUntil) {
        blockedUntilCache.put(clientIp, blockedUntil);
        failedAttemptsCache.invalidate(clientIp);
    }

    @Override
    public void clear(String clientIp) {
        failedAttemptsCache.invalidate(clientIp);
        blockedUntilCache.invalidate(clientIp);
    }

    private static final class BlockedUntilExpiry implements Expiry<String, Instant> {
        @Override
        public long expireAfterCreate(String key, Instant blockedUntil, long currentTime) {
            return nanosUntil(blockedUntil);
        }

        @Override
        public long expireAfterUpdate(String key, Instant blockedUntil, long currentTime, long currentDuration) {
            return nanosUntil(blockedUntil);
        }

        @Override
        public long expireAfterRead(String key, Instant blockedUntil, long currentTime, long currentDuration) {
            return currentDuration;
        }

        private static long nanosUntil(Instant blockedUntil) {
            var remain = Duration.between(Instant.now(), blockedUntil).toNanos();
            // Caffeine 要求正数；到期或过期值返回最小正数使其尽快淘汰。
            return remain > 0 ? remain : TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS);
        }
    }
}
