package cool.houge.mahu.shared.dto;

public record PresignedUploadResult(String objectId, String objectKey, String uploadUrl, String accessUrl) {}
