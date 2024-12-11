package cool.houge.mahu.remote.aliyun;

import lombok.Data;

import java.util.List;

/// @author ZY (kzou227@qq.com)
@Data
public class DirectUploadRequest {

    /// 阿里云访问密钥 ID.
    private String accessKeyId;
    /// 访问阿里云安全密钥.
    private String accessKeySecret;
    /// 有效期（单位：秒）.
    private int durationSeconds;
    /// 桶.
    private String bucket;
    /// 文件最小限制.
    private long minFileSize;
    /// 文件大小限制.
    private long maxFileSize;
    /// 支持上传文件的类型.
    private List<String> contentTypes;
    /// 文件名称.
    private String key;
}
