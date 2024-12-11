package cool.houge.mahu.admin.service;

import lombok.Data;

///
/// @author ZY (kzou227@qq.com)
@Data
public class MakeOssDirectUploadResult {

    /// 上传地址
    private String endpoint;
    /// 访问地址
    private String accessUrl;
    /// 文件名
    private String key;
    /// 策略
    private String policy;
    /// 访问键
    private String accessKeyId;
    /// 签名
    private String signature;
}
