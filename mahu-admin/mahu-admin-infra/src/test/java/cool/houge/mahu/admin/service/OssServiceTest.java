package cool.houge.mahu.admin.service;

import static org.assertj.core.api.Assertions.assertThat;

import cool.houge.mahu.admin.TestBase;
import cool.houge.mahu.config.OssKind;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

///
/// @author ZY (kzou227@qq.com)
class OssServiceTest extends TestBase {

    @Inject
    OssService ossService;

    @Test
    void presignedUpload() {
        var payload = new PresignedUploadPayload();
        payload.setKind(OssKind.ADMIN_AVATAR).setAdminId(2L).setFileName("hello.png");
        var presignedUrl = ossService.presignedUpload(payload);
        assertThat(presignedUrl).isNotNull();
    }
}
