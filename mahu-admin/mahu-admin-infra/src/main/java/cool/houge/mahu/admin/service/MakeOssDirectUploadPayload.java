package cool.houge.mahu.admin.service;

import cool.houge.mahu.config.OssKind;
import lombok.Data;

///
/// @author ZY (kzou227@qq.com)
@Data
public class MakeOssDirectUploadPayload {

    private OssKind ossKind;
    private String fileName;
}
