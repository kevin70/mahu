package cool.houge.mahu.shared;

import com.google.common.collect.ImmutableMap;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.common.LazyValue;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.Value;
import org.jspecify.annotations.NonNull;

@Value
@Builder
public class ImmutableDictGroup {

    Helper helper = new Helper();

    String id;
    String name;
    String description;
    boolean enabled;
    int visibility;
    boolean preset;
    List<@NonNull ImmutableDict> dicts;

    public boolean isPrivate() {
        return visibility == 0;
    }

    public boolean isPublic() {
        return visibility == 1;
    }

    public boolean isRestricted() {
        return visibility == 2;
    }

    public ImmutableDict byValue(String value) {
        var b = helper.dictValueMap.get().get(value);
        if (b == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺失字典%s: %s", id, value);
        }
        return b;
    }

    private class Helper {

        LazyValue<Map<String, ImmutableDict>> dictValueMap = LazyValue.create(() -> {
            return dicts.stream().collect(ImmutableMap.toImmutableMap(ImmutableDict::getValue, Function.identity()));
        });
    }
}
