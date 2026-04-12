package cool.houge.mahu.model.result;

import lombok.Builder;
import lombok.Value;

/// 预签名上传结果
///
/// 包含生成的预签名 URL 和访问 URL 等信息。
///
/// @author ZY (kzou227@qq.com)
@Value
@Builder
public class PresignedUploadResult {

    /// 对象 ID
    String objectId;
    /// 对象键（MinIO 中的完整路径）
    String objectKey;
    /// 预签名上传 URL（PUT 方法，用于上传文件）
    String uploadUrl;
    /// 访问 URL（公开对象为直接 URL，私有对象为预签名 URL）
    String accessUrl;
}
