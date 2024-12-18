package cool.houge.mahu.admin.service;

import cool.houge.mahu.config.OssKind;
import lombok.Data;

/// OSS 直接上传数据
///
/// @author ZY (kzou227@qq.com)
@Data
public class MakeOssDirectUploadPayload {

    /// 上传类型
    private OssKind kind;
    /// 文件名称
    private String fileName;
    /// 前缀限制
    private String prefixLimit;
}
