package cool.houge.mahu.shared.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.entity.sys.IdPhoto;
import cool.houge.mahu.entity.sys.StoredObject;
import cool.houge.mahu.repository.sys.IdPhotoRepository;
import cool.houge.mahu.repository.sys.StoredObjectRepository;
import cool.houge.mahu.shared.dto.PresignedUploadPayload;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/// {@link OssSharedService} 单元测试。
///
/// 通过 {@link Services#set} 在每个测试方法的 Registry 中注入 {@link OssHelper}、
/// {@link StoredObjectRepository} 和 {@link IdPhotoRepository} mock，
/// 再由 Helidon DI 将 {@link OssSharedService} 作为方法参数注入。
@Testing.Test(perMethod = true)
class OssSharedServiceTest {

    private final OssHelper ossHelper = mock(OssHelper.class);
    private final StoredObjectRepository storedObjectRepository = mock(StoredObjectRepository.class);
    private final IdPhotoRepository idPhotoRepository = mock(IdPhotoRepository.class);

    /// 在 OssSharedService 被首次请求前，将三个 mock 实例注入当前方法的 Registry。
    @BeforeEach
    void setUp() {
        Services.set(OssHelper.class, ossHelper);
        Services.set(StoredObjectRepository.class, storedObjectRepository);
        Services.set(IdPhotoRepository.class, idPhotoRepository);
    }

    @Test
    void presignedUpload_for_stored_object_saves_pending_and_returns_access_url(OssSharedService service) {
        when(ossHelper.presignedUploadUrl(anyString(), anyMap())).thenReturn("https://upload.local/u");
        when(ossHelper.accessUrl(anyString())).thenReturn("https://cdn.local/file");

        var payload = new PresignedUploadPayload().setFileName("avatar.png");
        var result = service.presignedUpload(StoredObject.Type.ADMIN_AVATAR, payload);

        var objectCaptor = ArgumentCaptor.forClass(StoredObject.class);
        verify(storedObjectRepository).save(objectCaptor.capture());
        var storedObject = objectCaptor.getValue();

        assertNotNull(result.objectId());
        assertTrue(result.objectKey().startsWith(StoredObject.Type.ADMIN_AVATAR.getPrefix() + "/"));
        assertTrue(result.objectKey().endsWith(".png"));
        assertEquals("https://upload.local/u", result.uploadUrl());
        assertEquals("https://cdn.local/file", result.accessUrl());

        assertEquals(result.objectId(), storedObject.getId());
        assertEquals(StoredObject.Type.ADMIN_AVATAR, storedObject.getType());
        assertEquals(Status.PENDING.getCode(), storedObject.getStatus());
        assertEquals(result.objectKey(), storedObject.getObjectKey());
    }

    @Test
    void presignedUpload_for_id_photo_saves_pending_and_returns_presigned_get_url(OssSharedService service) {
        when(ossHelper.presignedUploadUrl(anyString(), anyMap())).thenReturn("https://upload.local/u");
        when(ossHelper.presignedGetUrl(anyString())).thenReturn("https://download.local/d");

        var payload = new PresignedUploadPayload().setFileName("id.jpg");
        var result = service.presignedUpload(IdPhoto.Type.DEFAULT, payload);

        var photoCaptor = ArgumentCaptor.forClass(IdPhoto.class);
        verify(idPhotoRepository).save(photoCaptor.capture());
        var idPhoto = photoCaptor.getValue();

        assertNotNull(result.objectId());
        assertTrue(result.objectKey().startsWith(IdPhoto.Type.DEFAULT.getPrefix() + "/"));
        assertTrue(result.objectKey().endsWith(".jpg"));
        assertEquals("https://upload.local/u", result.uploadUrl());
        assertEquals("https://download.local/d", result.accessUrl());

        assertEquals(result.objectId(), idPhoto.getId());
        assertEquals(IdPhoto.Type.DEFAULT, idPhoto.getType());
        assertEquals(Status.PENDING.getCode(), idPhoto.getStatus());
        assertEquals(result.objectKey(), idPhoto.getObjectKey());
    }

    @Test
    void presignedGetUrlByStoredObject_throws_not_found_when_missing(OssSharedService service) {
        when(storedObjectRepository.findById("missing")).thenReturn(null);

        var ex = assertThrows(BizCodeException.class, () -> service.presignedGetUrlByStoredObject("missing"));

        assertEquals(BizCodes.NOT_FOUND, ex.getCode());
    }

    @Test
    void presignedGetUrlByIdPhoto_returns_url_when_found(OssSharedService service) {
        var photo = new IdPhoto().setId("id-1").setObjectKey("/d/id-1.png");
        when(idPhotoRepository.findById("id-1")).thenReturn(photo);
        when(ossHelper.presignedGetUrl("/d/id-1.png")).thenReturn("https://download.local/id-1");

        var url = service.presignedGetUrlByIdPhoto("id-1");

        assertEquals("https://download.local/id-1", url);
    }
}

