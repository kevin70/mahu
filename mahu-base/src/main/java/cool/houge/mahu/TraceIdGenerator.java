package cool.houge.mahu;

import java.util.concurrent.ThreadLocalRandom;

/// 追踪 ID 生成器
///
/// @author ZY (kzou227@qq.com)
public final class TraceIdGenerator {

    /// 生成任务追踪 ID
    public static String generate() {
        var random = ThreadLocalRandom.current();
        var bytes = new byte[16];
        random.nextBytes(bytes);
        return Base58.encode(bytes);
    }
}
