package cool.houge.mahu.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

/// RoaringBitmapUtils 类提供了与 roaringbitmap 相关的工具方法。
///
/// @author ZY (kzou227@qq.com)
public class RoaringBitmapUtils {

    private RoaringBitmapUtils() {
        throw new IllegalStateException("Utility class");
    }

    /// 将 Roaring64NavigableMap 对象序列化为字节数组
    ///
    /// 该方法接受一个 `Roaring64NavigableMap` 对象 `bean`，并将其序列化为字节数组。
    /// - 如果 `bean` 为 null 或为空，则返回一个空的字节数组。
    /// - 否则，使用 `DataOutputStream` 和 `ByteArrayOutputStream` 将 `Roaring64NavigableMap` 对象序列化为字节数组。
    /// - 如果在序列化过程中发生 `IOException`，则抛出 `UncheckedIOException`。
    public static Roaring64NavigableMap toRoaring64NavigableMap(byte[] bytes) {
        var r = new Roaring64NavigableMap();
        if (bytes == null || bytes.length == 0) {
            return r;
        }

        try {
            r.deserialize(new DataInputStream(new ByteArrayInputStream(bytes)));
            return r;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /// 将字节数组转换为 Roaring64NavigableMap
    ///
    /// 该方法接受一个字节数组 `bytes`，并将其反序列化为 `Roaring64NavigableMap` 对象。
    /// - 如果 `bytes` 为 null 或长度为 0，则返回一个新的空的 `Roaring64NavigableMap` 对象。
    /// - 否则，使用 `DataInputStream` 和 `ByteArrayInputStream` 将字节数组反序列化为 `Roaring64NavigableMap` 对象。
    /// - 如果在反序列化过程中发生 `IOException`，则抛出 `UncheckedIOException`。
    public static byte[] toBytes(Roaring64NavigableMap bean) {
        if (bean == null || bean.isEmpty()) {
            return new byte[0];
        }

        try {
            var baos = new ByteArrayOutputStream();
            bean.serialize(new DataOutputStream(baos));
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
