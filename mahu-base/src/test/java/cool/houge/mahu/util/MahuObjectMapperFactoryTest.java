package cool.houge.mahu.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class MahuObjectMapperFactoryTest {

    @Test
    void createDefault_ignores_unknown_properties_and_serializes_java_time() throws Exception {
        var objectMapper = MahuObjectMapperFactory.createDefault();

        var payload = objectMapper.readValue("{\"name\":\"mahu\",\"unknown\":1}", Payload.class);
        assertThat(payload.name).isEqualTo("mahu");

        var json = objectMapper.writeValueAsString(new Payload("mahu", Instant.parse("2026-04-10T00:00:00Z"), false));
        assertThat(json).contains("\"createdAt\":\"2026-04-10T00:00:00Z\"");
        assertThat(json).contains("\"enabled\":false");
    }

    @Test
    void create_nonDefault_omits_default_fields() throws Exception {
        var objectMapper = MahuObjectMapperFactory.create(JsonInclude.Include.NON_DEFAULT);

        var json = objectMapper.writeValueAsString(new Payload(null, null, false));

        assertThat(json).isEqualTo("{}");
    }

    static final class Payload {

        public String name;
        public Instant createdAt;
        public boolean enabled;

        Payload() {}

        Payload(String name, Instant createdAt, boolean enabled) {
            this.name = name;
            this.createdAt = createdAt;
            this.enabled = enabled;
        }
    }
}
