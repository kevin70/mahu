package cool.houge.mahu.shared.dto;

/// 预签名上传响应
///
/// @param presignedUploadUrl 上传地址
/// @param objectKey 对象的完整 key（含前缀）
/// @param previewUrl 上传成功后预览地址
/// @author ZY (kzou227@qq.com)
public record PresignedUploadResult(String presignedUploadUrl, String objectKey, String previewUrl) {}
