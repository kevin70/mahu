package cool.houge.blma.shared.service;

import static org.assertj.core.api.Assertions.assertThat;

import cool.houge.blma.config.OssConfig;
import cool.houge.blma.config.OssKind;
import cool.houge.blma.shared.dto.PresignedUploadPayload;
import io.hypersistence.tsid.TSID;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

///
/// @author ZY (kzou227@qq.com)
class SharedOssServiceTest {

    SharedOssService ossService;

    MinioClient minioClient;

    OssConfig ossConfig;

    @Test
    void presignedUpload() {
        var payload = new PresignedUploadPayload();
        payload.setKind(OssKind.ADMIN_AVATAR).setAdminId("SIT.ME").setFileName("hello.png");
        var presignedUrl = ossService.presignedUpload(payload);
        assertThat(presignedUrl).isNotNull();
    }

    @Disabled("仅手动运行")
    @Test
    void testUpload() throws IOException, InterruptedException {
        var payload = new PresignedUploadPayload();
        payload.setKind(OssKind.PCS_ASSET).setFileName("hello.png");
        var presignedUrl = ossService.presignedUpload(payload);
        try (var httpClient =
                HttpClient.newBuilder().connectTimeout(Duration.ofMinutes(1)).build()) {
            var f = System.getProperty("user.home") + "/Downloads/GW8yZXCaYAA_tCE.jpeg";
            var request = HttpRequest.newBuilder(URI.create(presignedUrl.presignedUploadUrl()))
                    .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(f)))
                    .header("content-type", "image/jpeg")
                    .timeout(Duration.ofMinutes(1))
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            assertThat(response.statusCode()).isEqualTo(200);
        }
    }

    @Disabled("仅手动运行")
    @Test
    void testAliyunUpload()
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
                    NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
                    InternalException {
        var args = UploadObjectArgs.builder()
                .bucket(ossConfig.bucket())
                .filename(System.getProperty("user.home") + "/Downloads/GW8yZXCaYAA_tCE.jpeg")
                .object(OssKind.PCS_ASSET.getPrefix() + "/" + TSID.fast())
                .build();
        var resp = minioClient.uploadObject(args);
        System.out.println(resp);
        assertThat(resp).isNotNull();
    }
}
