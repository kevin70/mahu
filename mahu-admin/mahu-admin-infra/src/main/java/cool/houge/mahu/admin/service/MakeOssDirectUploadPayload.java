package cool.houge.mahu.admin.service;

import cool.houge.mahu.config.OssKind;
import lombok.Data;

/// OSS 直接上传数据
///
/// @author ZY (kzou227@qq.com)
@Data
public class MakeOssDirectUploadPayload {

    private OssKind kind;
    private String fileName;
}
