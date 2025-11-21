package cool.houge.mahu.shared.dto;

/// 预签名上传响应
///
/// @param presignedUploadUrl 上传地址
/// @param objectName 对象存储的名称
/// @param previewUrl 上传成功后预览地址
/// @author ZY (kzou227@qq.com)
public record PresignedUploadResult(String presignedUploadUrl, String objectName, String previewUrl) {}
