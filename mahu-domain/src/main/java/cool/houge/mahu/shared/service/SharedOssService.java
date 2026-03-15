package cool.houge.mahu.shared.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.io.Files;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.Status;
import cool.houge.mahu.entity.sys.IdPhoto;
import cool.houge.mahu.entity.sys.StoredObject;
import cool.houge.mahu.shared.dto.PresignedUploadPayload;
import cool.houge.mahu.shared.dto.PresignedUploadResult;
import cool.houge.mahu.repository.sys.IdPhotoRepository;
import cool.houge.mahu.repository.sys.StoredObjectRepository;
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
    private final StoredObjectRepository storedObjectRepository;
    private final IdPhotoRepository idPhotoRepository;

    /// 预上传文件
    ///
    /// @return 预签名上传
    public PresignedUploadResult presignedUpload(StoredObject.Type type, PresignedUploadPayload payload) {
        var id = UlidCreator.getMonotonicUlid().toString();
        var objectKey = type.buildObjectKey(id, Files.getFileExtension(payload.getFileName()));
        var uploadUrl = ossHelper.presignedUploadUrl(objectKey, Map.of());

        // 保存预约上传文件
        var storeObject = new StoredObject()
                .setId(id)
                .setType(type)
                .setObjectKey(objectKey)
                .setStatus(Status.PENDING.getCode());
        storedObjectRepository.save(storeObject);

        // 公共的资源直接使用公共访问链接
        var accessUrl = type.isOpen() ? ossHelper.accessUrl(objectKey) : ossHelper.presignedGetUrl(objectKey);
        return new PresignedUploadResult(storeObject.getId(), objectKey, uploadUrl, accessUrl);
    }

    /// 获取[StoredObject]访问 URL
    ///
    /// @param objectId 对象 ID
    /// @return 访问 URL
    public String presignedGetUrlByStoredObject(String objectId) {
        var obj = storedObjectRepository.findById(objectId);
        if (obj == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到 StoredObject");
        }
        return presignedGetUrl(obj.getObjectKey());
    }

    /// 预上传证件照
    ///
    /// @return 预签名上传
    public PresignedUploadResult presignedUpload(IdPhoto.Type type, PresignedUploadPayload payload) {
        var id = UlidCreator.getMonotonicUlid().toString();
        var objectKey = type.buildObjectKey(id, Files.getFileExtension(payload.getFileName()));
        var uploadUrl = ossHelper.presignedUploadUrl(objectKey, Map.of());

        var idPhoto =
                new IdPhoto().setId(id).setType(type).setObjectKey(objectKey).setStatus(Status.PENDING.getCode());
        idPhotoRepository.save(idPhoto);
        return new PresignedUploadResult(idPhoto.getId(), objectKey, uploadUrl, ossHelper.presignedGetUrl(objectKey));
    }

    /// 获取[IdPhoto]访问 URL
    ///
    /// @param objectId 对象 ID
    /// @return 访问 URL
    public String presignedGetUrlByIdPhoto(String objectId) {
        var obj = idPhotoRepository.findById(objectId);
        if (obj == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到 IdPhoto");
        }
        return presignedGetUrl(obj.getObjectKey());
    }

    /// 获取预签名 GET URL
    ///
    /// @param objectKey 对象的完整 key（含前缀）
    public String presignedGetUrl(String objectKey) {
        return ossHelper.presignedGetUrl(objectKey);
    }
}
