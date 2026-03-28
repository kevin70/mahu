package cool.houge.mahu.shared.dto;

/// 预签名上传结果
///
/// 包含生成的预签名 URL 和访问 URL 等信息。
///
/// @param objectId 对象 ID
/// @param objectKey 对象键（MinIO 中的完整路径）
/// @param uploadUrl 预签名上传 URL（PUT 方法，用于上传文件）
/// @param accessUrl 访问 URL（公开对象为直接 URL，私有对象为预签名 URL）
///
/// @author ZY (kzou227@qq.com)
public record PresignedUploadResult(String objectId, String objectKey, String uploadUrl, String accessUrl) {}
