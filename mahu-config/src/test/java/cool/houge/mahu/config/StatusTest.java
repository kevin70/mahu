package cool.houge.mahu.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class StatusTest {

    @Test
    void fromCode_returns_matching_status() {
        assertThat(Status.fromCode(200)).contains(Status.ACTIVE);
        assertThat(Status.fromCode(900)).contains(Status.FAILED);
    }

    @Test
    void fromCode_returns_empty_when_code_unknown() {
        assertThat(Status.fromCode(9999)).isEmpty();
        assertThat(Status.fromCode(null)).isEmpty();
    }

    @Test
    void valueOf_throws_when_code_invalid() {
        assertThatThrownBy(() -> Status.valueOf(9999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("无效的状态码");
    }

    @Test
    void isValidCode_returns_expected_result() {
        assertThat(Status.isValidCode(130)).isTrue();
        assertThat(Status.isValidCode(9999)).isFalse();
        assertThat(Status.isValidCode(null)).isFalse();
    }

    @Test
    void eq_and_neq_compare_against_code() {
        assertThat(Status.ACTIVE.eq(200)).isTrue();
        assertThat(Status.ACTIVE.neq(720)).isTrue();
    }
}
