package cool.houge.mahu.admin;

import org.junit.jupiter.api.Test;

import static cool.houge.mahu.admin.Permits.AUDIT_LOG;
import static cool.houge.mahu.admin.Permits.DICT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ZY (kzou227@qq.com)
 */
class PermitsTest {

    @Test
    void execute() {
        assertThat(DICT.canRead()).isTrue();
        assertThat(DICT.canWrite()).isTrue();
        assertThat(DICT.canDelete()).isTrue();

        assertThat(AUDIT_LOG.canRead()).isTrue();
        assertThat(AUDIT_LOG.canWrite()).isFalse();
        assertThat(AUDIT_LOG.canDelete()).isFalse();
    }
}
