package cool.houge.mahu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ObjectMapperProviderTest {

    @Test
    void get_applies_stable_json_defaults() throws Exception {
        var mapper = new ObjectMapperProvider().get();

        var payload = mapper.readValue(
                "{\"name\":\"mahu\",\"createdAt\":\"2026-04-11T00:00:00Z\",\"unknown\":1}", Payload.class);
        var json = mapper.writeValueAsString(new Payload(null, Instant.parse("2026-04-11T00:00:00Z"), false));

        assertEquals("mahu", payload.name);
        assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertFalse(mapper.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE));
        assertFalse(mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertTrue(json.contains("\"createdAt\":\"2026-04-11T00:00:00Z\""));
        assertTrue(json.contains("\"enabled\":false"));
        assertFalse(json.contains("\"name\""));
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
