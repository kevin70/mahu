package cool.houge.mahu.shared.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.io.Files;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.entity.sys.IdPhoto;
import cool.houge.mahu.entity.sys.StoredObject;
import cool.houge.mahu.repository.sys.IdPhotoRepository;
import cool.houge.mahu.repository.sys.StoredObjectRepository;
import cool.houge.mahu.shared.dto.PresignedUploadPayload;
import cool.houge.mahu.shared.dto.PresignedUploadResult;
import io.helidon.service.registry.Service;
import java.util.Map;
import lombok.AllArgsConstructor;

@Service.Singleton
@AllArgsConstructor
public class OssSharedService {

    private final OssHelper ossHelper;
    private final StoredObjectRepository storedObjectRepository;
    private final IdPhotoRepository idPhotoRepository;

    public PresignedUploadResult presignedUpload(StoredObject.Type type, PresignedUploadPayload payload) {
        var id = UlidCreator.getMonotonicUlid().toString();
        var objectKey = type.buildObjectKey(id, Files.getFileExtension(payload.getFileName()));
        var uploadUrl = ossHelper.presignedUploadUrl(objectKey, Map.of());

        var storeObject = new StoredObject()
                .setId(id)
                .setType(type)
                .setObjectKey(objectKey)
                .setStatus(Status.PENDING.getCode());
        storedObjectRepository.save(storeObject);

        var accessUrl = type.isOpen() ? ossHelper.accessUrl(objectKey) : ossHelper.presignedGetUrl(objectKey);
        return new PresignedUploadResult(storeObject.getId(), objectKey, uploadUrl, accessUrl);
    }

    public String presignedGetUrlByStoredObject(String objectId) {
        var obj = storedObjectRepository.findById(objectId);
        if (obj == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到 StoredObject");
        }
        return presignedGetUrl(obj.getObjectKey());
    }

    public PresignedUploadResult presignedUpload(IdPhoto.Type type, PresignedUploadPayload payload) {
        var id = UlidCreator.getMonotonicUlid().toString();
        var objectKey = type.buildObjectKey(id, Files.getFileExtension(payload.getFileName()));
        var uploadUrl = ossHelper.presignedUploadUrl(objectKey, Map.of());

        var idPhoto =
                new IdPhoto().setId(id).setType(type).setObjectKey(objectKey).setStatus(Status.PENDING.getCode());
        idPhotoRepository.save(idPhoto);
        return new PresignedUploadResult(idPhoto.getId(), objectKey, uploadUrl, ossHelper.presignedGetUrl(objectKey));
    }

    public String presignedGetUrlByIdPhoto(String objectId) {
        var obj = idPhotoRepository.findById(objectId);
        if (obj == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到 IdPhoto");
        }
        return presignedGetUrl(obj.getObjectKey());
    }

    public String presignedGetUrl(String objectKey) {
        return ossHelper.presignedGetUrl(objectKey);
    }
}
