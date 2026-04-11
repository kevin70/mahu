package cool.houge.mahu.shared.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class OssHelperTest {

    private final MinioClient minioClient = mock(MinioClient.class);
    private final OssHelper helper = new OssHelper(config("https://cdn.local"), minioClient);

    @Test
    void presignedUploadUrl_builds_put_request_with_query_params() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("https://upload.local/u");

        var result = helper.presignedUploadUrl("p/admin/avatars/file.png", Map.of("x-test", "1"));

        var argsCaptor = ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
        verify(minioClient).getPresignedObjectUrl(argsCaptor.capture());
        var args = argsCaptor.getValue();

        assertAll(
                () -> assertEquals("https://upload.local/u", result),
                () -> assertEquals(Http.Method.PUT, args.method()),
                () -> assertEquals("mahu-dev", args.bucket()),
                () -> assertEquals("p/admin/avatars/file.png", args.object()),
                () -> assertEquals((int) TimeUnit.DAYS.toSeconds(1), args.expiry()),
                () -> assertEquals("1", args.extraQueryParams().getFirst("x-test")));
    }

    @Test
    void presignedUploadUrl_uses_empty_query_params_when_null() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("https://upload.local/u");

        var result = helper.presignedUploadUrl("p/admin/avatars/file.png", null);

        var argsCaptor = ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
        verify(minioClient).getPresignedObjectUrl(argsCaptor.capture());
        var args = argsCaptor.getValue();

        assertAll(
                () -> assertEquals("https://upload.local/u", result),
                () -> assertEquals(0, args.extraQueryParams().entries().size()));
    }

    @Test
    void presignedGetUrl_builds_get_request() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("https://download.local/d");

        var result = helper.presignedGetUrl("/d/file.jpg");

        var argsCaptor = ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
        verify(minioClient).getPresignedObjectUrl(argsCaptor.capture());
        var args = argsCaptor.getValue();

        assertAll(
                () -> assertEquals("https://download.local/d", result),
                () -> assertEquals(Http.Method.GET, args.method()),
                () -> assertEquals("mahu-dev", args.bucket()),
                () -> assertEquals("/d/file.jpg", args.object()),
                () -> assertEquals((int) TimeUnit.DAYS.toSeconds(1), args.expiry()),
                () -> assertEquals(0, args.extraQueryParams().entries().size()));
    }

    @Test
    void accessUrl_joins_base_and_relative_object_key() {
        var result = helper.accessUrl("p/admin/avatars/file.png");

        assertEquals("https://cdn.local/p/admin/avatars/file.png", result);
    }

    @Test
    void accessUrl_keeps_single_separator_when_object_key_has_leading_slash() {
        var result = helper.accessUrl("/d/file.jpg");

        assertEquals("https://cdn.local/d/file.jpg", result);
    }

    @Test
    void accessUrl_normalizes_trailing_slash_in_config() {
        var helperWithTrailingSlash = new OssHelper(config("https://cdn.local/"), minioClient);

        var result = helperWithTrailingSlash.accessUrl("p/admin/avatars/file.png");

        assertEquals("https://cdn.local/p/admin/avatars/file.png", result);
    }

    @Test
    void presignedGetUrl_wraps_minio_error_with_download_context() throws Exception {
        var cause = new RuntimeException("boom");
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(cause);

        var ex = assertThrows(BizCodeException.class, () -> helper.presignedGetUrl("p/file.png"));

        assertAll(
                () -> assertEquals(BizCodes.UNAVAILABLE, ex.getCode()),
                () -> assertEquals("获取预签名下载URL错误", ex.getRawMessage()),
                () -> assertSame(cause, ex.getCause()));
    }

    @Test
    void presignedGetUrl_throws_invalid_argument_when_object_key_is_blank() {
        var ex = assertThrows(BizCodeException.class, () -> helper.presignedGetUrl(" "));

        assertAll(
                () -> assertEquals(BizCodes.INVALID_ARGUMENT, ex.getCode()),
                () -> assertEquals("objectKey 不能为空", ex.getRawMessage()));
    }

    @Test
    void presignedUploadUrl_throws_invalid_argument_when_query_param_key_is_blank() {
        var ex = assertThrows(
                BizCodeException.class, () -> helper.presignedUploadUrl("p/admin/avatars/file.png", Map.of("", "1")));

        assertAll(
                () -> assertEquals(BizCodes.INVALID_ARGUMENT, ex.getCode()),
                () -> assertEquals("生成预签名上传URL参数非法", ex.getRawMessage()));
    }

    private static Config config(String accessUrl) {
        return Config.just(ConfigSources.create(Map.of(
                        "oss.endpoint", "http://127.0.0.1:19000",
                        "oss.access-key", "minioadmin",
                        "oss.secret-key", "minioadmin",
                        "oss.region", "us-east-1",
                        "oss.bucket", "mahu-dev",
                        "oss.access-url", accessUrl))
                .build());
    }
}
