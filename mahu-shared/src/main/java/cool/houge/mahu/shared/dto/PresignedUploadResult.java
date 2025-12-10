package cool.houge.mahu.shared.dto;

/// 预签名上传响应
///
/// @param objectId 对象 ID
/// @param presignedUploadUrl 上传地址
/// @param objectKey 对象的完整 key（含前缀）
/// @param accessUrl 上传成功后访问 URL
/// @author ZY (kzou227@qq.com)
public record PresignedUploadResult(long objectId, String presignedUploadUrl, String objectKey, String accessUrl) {}
