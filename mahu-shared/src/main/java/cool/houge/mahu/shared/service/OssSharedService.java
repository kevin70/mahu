package cool.houge.mahu.shared.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.io.Files;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.entity.sys.IdPhoto;
import cool.houge.mahu.entity.sys.StoredObject;
import cool.houge.mahu.model.command.PresignedUploadCommand;
import cool.houge.mahu.model.result.PresignedUploadResult;
import cool.houge.mahu.repository.sys.IdPhotoRepository;
import cool.houge.mahu.repository.sys.StoredObjectRepository;
import io.helidon.service.registry.Service;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;

/// 对象存储（OSS）共享服务
///
/// 提供与 MinIO 对象存储交互的统一接口，包括：
/// - 生成预签名上传 URL（支持 StoredObject 和 IdPhoto）
/// - 生成预签名下载 URL
/// - 管理存储对象元数据
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class OssSharedService {

    private static final Map<String, String> EMPTY_QUERY_PARAMS = Map.of();

    private final OssHelper ossHelper;
    private final StoredObjectRepository storedObjectRepository;
    private final IdPhotoRepository idPhotoRepository;

    /// 为通用存储对象生成预签名上传 URL
    ///
    /// @param type 存储对象类型
    /// @param payload 上传请求负载（包含文件名等）
    /// @return 包含预签名 URL 和访问 URL 的响应对象
    public PresignedUploadResult presignedUpload(StoredObject.Type type, PresignedUploadCommand payload) {
        return createPresignedUpload(
                payload.getFileName(),
                type::buildObjectKey,
                (id, objectKey) -> new StoredObject()
                        .setId(id)
                        .setType(type)
                        .setObjectKey(objectKey)
                        .setStatus(Status.PENDING.getCode()),
                storedObjectRepository::save,
                objectKey -> type.isOpen() ? ossHelper.accessUrl(objectKey) : ossHelper.presignedGetUrl(objectKey));
    }

    /// 通过已保存的 StoredObject 获取预签名下载 URL
    ///
    /// @param objectId 存储对象 ID
    /// @return 预签名下载 URL
    /// @throws BizCodeException 当对象不存在时抛出异常
    public String presignedGetUrlByStoredObject(String objectId) {
        return presignedGetUrlByObjectId(
                objectId, storedObjectRepository::findById, StoredObject::getObjectKey, "未找到 StoredObject");
    }

    /// 为身份证照片生成预签名上传 URL
    ///
    /// @param type 身份证照片类型
    /// @param payload 上传请求负载
    /// @return 包含预签名 URL 和访问 URL 的响应对象
    public PresignedUploadResult presignedUpload(IdPhoto.Type type, PresignedUploadCommand payload) {
        return createPresignedUpload(
                payload.getFileName(),
                type::buildObjectKey,
                (id, objectKey) -> new IdPhoto()
                        .setId(id)
                        .setType(type)
                        .setObjectKey(objectKey)
                        .setStatus(Status.PENDING.getCode()),
                idPhotoRepository::save,
                ossHelper::presignedGetUrl);
    }

    /// 通过已保存的 IdPhoto 获取预签名下载 URL
    ///
    /// @param objectId 身份证照片 ID
    /// @return 预签名下载 URL
    /// @throws BizCodeException 当照片不存在时抛出异常
    public String presignedGetUrlByIdPhoto(String objectId) {
        return presignedGetUrlByObjectId(objectId, idPhotoRepository::findById, IdPhoto::getObjectKey, "未找到 IdPhoto");
    }

    /// 为指定对象生成预签名下载 URL
    ///
    /// @param objectKey 对象键
    /// @return 预签名下载 URL
    public String presignedGetUrl(String objectKey) {
        return ossHelper.presignedGetUrl(objectKey);
    }

    private <T> PresignedUploadResult createPresignedUpload(
            String fileName,
            BinaryOperator<String> objectKeyBuilder,
            BiFunction<String, String, T> entityFactory,
            Consumer<T> entitySaver,
            UnaryOperator<String> accessUrlResolver) {
        var id = newObjectId();
        var objectKey = objectKeyBuilder.apply(id, Files.getFileExtension(fileName));
        var uploadUrl = ossHelper.presignedUploadUrl(objectKey, EMPTY_QUERY_PARAMS);
        entitySaver.accept(entityFactory.apply(id, objectKey));
        return PresignedUploadResult.builder()
                .objectId(id)
                .objectKey(objectKey)
                .uploadUrl(uploadUrl)
                .accessUrl(accessUrlResolver.apply(objectKey))
                .build();
    }

    private <T> String presignedGetUrlByObjectId(
            String objectId, Function<String, T> finder, Function<T, String> objectKeyGetter, String notFoundMessage) {
        var obj = finder.apply(objectId);
        if (obj == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, notFoundMessage);
        }
        return presignedGetUrl(objectKeyGetter.apply(obj));
    }

    private static String newObjectId() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}
