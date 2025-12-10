package cool.houge.mahu.shared.dto;

import lombok.Data;

/// 预签名上传
///
/// @author ZY (kzou227@qq.com)
@Data
public class PresignedUploadPayload {

    /// 上传的文件名称
    private String fileName;
}
