package cool.houge.mahu;

import java.util.concurrent.ThreadLocalRandom;

/// 随机工具类
///
/// @author ZY (kzou227@qq.com)
public final class RandomUtils {

    /// 生成16位的随机字符串
    public static String nonce() {
        return nonce(16);
    }

    /// 生成指定位数的随机字符串
    public static String nonce(int bits) {
        var bytes = new byte[bits];
        var random = ThreadLocalRandom.current();
        random.nextBytes(bytes);
        return Base58.encode(bytes);
    }
}
