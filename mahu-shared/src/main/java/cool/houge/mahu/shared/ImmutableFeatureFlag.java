package cool.houge.mahu.shared;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImmutableFeatureFlag {
    int id;
    String code;
    String name;
    String description;
    boolean enabled;
    boolean preset;
    Instant enableAt;
    Instant disableAt;
    int ordering;

    public boolean isActive() {
        return isActive(Instant.now());
    }

    public boolean isActive(Instant now) {
        if (!enabled) {
            return false;
        }
        if (enableAt != null && now.isBefore(enableAt)) {
            return false;
        }
        if (disableAt != null && now.isAfter(disableAt)) {
            return false;
        }
        return true;
    }
}
