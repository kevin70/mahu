package cool.houge.mahu.util;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.common.context.Contexts;

/// 元数据
///
/// @author ZY (kzou227@qq.com)
public interface Metadata {

    /// 客户端请求地址
    String clientAddr();

    /// 客户端请求 User-Agent
    ///
    /// 返回值最长为 512 个字符，超出部分由实现截断。
    String userAgent();

    /// 追踪 ID
    String traceId();

    /// 接口版本号
    default int apiVersion() {
        return 1;
    }

    /// 返回当前上下文中的元数据
    static Metadata current() {
        return Contexts.context()
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAVAILABLE, "缺少 Context"))
                .get(Metadata.class)
                .orElseThrow(() -> new BizCodeException(BizCodes.FAILED_PRECONDITION, "缺少 Metadata"));
    }
}
