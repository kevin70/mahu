package cool.houge.mahu.shared.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class CaffeineIpAuthFailureTrackerTest {

    private static final String IP = "10.0.0.1";

    private final CaffeineIpAuthFailureTracker sut = new CaffeineIpAuthFailureTracker();

    @Test
    void incrementFailure_同一Ip连续调用_返回递增计数() {
        var first = sut.incrementFailure(IP);
        var second = sut.incrementFailure(IP);
        var third = sut.incrementFailure(IP);

        assertEquals(1, first);
        assertEquals(2, second);
        assertEquals(3, third);
    }

    @Test
    void block_未过期封禁_可读取封禁截止时间() {
        var now = Instant.now();
        var blockedUntil = now.plusSeconds(120);
        sut.block(IP, blockedUntil);

        var rs = sut.blockedUntil(IP, now);

        assertTrue(rs.isPresent());
        assertEquals(blockedUntil, rs.get());
    }

    @Test
    void blockedUntil_已过期封禁_返回空并清理状态() {
        var now = Instant.now();
        sut.block(IP, now.minusSeconds(1));

        var rs = sut.blockedUntil(IP, now);
        var countAfterCleanup = sut.incrementFailure(IP);

        assertFalse(rs.isPresent());
        assertEquals(1, countAfterCleanup);
    }

    @Test
    void clear_清理失败计数与封禁状态() {
        var now = Instant.now();
        sut.incrementFailure(IP);
        sut.incrementFailure(IP);
        sut.block(IP, now.plusSeconds(300));

        sut.clear(IP);
        var blocked = sut.blockedUntil(IP, now);
        var count = sut.incrementFailure(IP);

        assertFalse(blocked.isPresent());
        assertEquals(1, count);
    }
}
