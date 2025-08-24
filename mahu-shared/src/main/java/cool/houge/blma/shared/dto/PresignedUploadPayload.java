package cool.houge.blma.shared.dto;

import cool.houge.mahu.config.OssKind;
import lombok.Data;

/// 预签名上传
///
/// @author ZY (kzou227@qq.com)
@Data
public class PresignedUploadPayload {

    /// 存储对象类型
    private OssKind kind;
    /// 上传的文件名称
    private String fileName;
    /// 管理员 ID
    ///
    /// 在以下类型需要的参数
    /// - [OssKind#ADMIN_AVATAR]
    private String adminId;
    /// 用户 ID
    ///
    /// 在以下类型需要的参数
    /// - [OssKind#USER_AVATAR]
    private String userId;
}
