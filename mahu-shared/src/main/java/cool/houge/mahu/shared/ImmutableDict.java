package cool.houge.mahu.shared;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImmutableDict {
    String groupId;
    int dc;
    String value;
    String label;
    boolean enabled;
    int ordering;
    String color;
    boolean preset;
}
