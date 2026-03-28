package cool.houge.mahu.shared.dto;

import lombok.Getter;
import lombok.Setter;

/// 预签名上传请求负载
///
/// 客户端调用预签名上传接口时的请求参数。
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
public class PresignedUploadPayload {
    /// 文件名（包含扩展名）
    private String fileName;
}
