package cool.houge.mahu;

import java.nio.ByteBuffer;
import java.util.UUID;

/// 追踪 ID 生成器
///
/// @author ZY (kzou227@qq.com)
public final class TraceIdGenerator {

    private TraceIdGenerator() {}

    /// 生成任务追踪 ID
    public static String generate() {
        var uuid = UUID.randomUUID();
        var buf = ByteBuffer.allocate(16);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        return Base58.encode(buf.array());
    }
}
