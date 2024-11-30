package cool.houge.mahu.common;

/// 元数据
///
/// @author ZY (kzou227@qq.com)
public interface Metadata {

    /// 客户端请求地址
    String clientAddr();

    /// 追踪 ID
    String traceId();
}
