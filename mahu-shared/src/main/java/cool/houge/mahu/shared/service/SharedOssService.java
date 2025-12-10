package cool.houge.mahu.shared.service;

import io.helidon.service.registry.Service;
import java.util.Map;
import lombok.AllArgsConstructor;

/// OSS存储服务
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class SharedOssService {

    private final OssHelper ossHelper;

    /// 预签名上传
    ///
    /// @return 预签名上传的 URL
    public String presignedUploadUrl(String objectKey, Map<String, String> queryParams) {
        return ossHelper.presignedUploadUrl(objectKey, queryParams);
    }

    /// 获取预签名 GET URL
    ///
    /// @param objectKey 对象的完整 key（含前缀）
    public String presignedGetUrl(String objectKey) {
        return ossHelper.presignedGetUrl(objectKey);
    }
}
