package cool.houge.mahu.remote.aliyun;

import lombok.Data;

/// @author ZY (kzou227@qq.com)
@Data
public class DirectUploadResponse {

    private String key;
    private String policy;
    private String signature;
}
