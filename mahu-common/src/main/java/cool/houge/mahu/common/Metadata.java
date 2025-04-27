package cool.houge.mahu.common;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.common.context.Contexts;

/// 元数据
///
/// @author ZY (kzou227@qq.com)
public interface Metadata {

    /// 客户端请求地址
    String clientAddr();

    /// 追踪 ID
    String traceId();

    /// 返回当前上下文中的元数据
    static Metadata metadata() {
        return Contexts.context()
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAVAILABLE, "缺少 Context"))
                .get(Metadata.class)
                .orElseThrow(() -> new BizCodeException(BizCodes.FAILED_PRECONDITION, "缺少 Metadata"));
    }
}
