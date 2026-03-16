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

/// 字典分组快照模型。
///
/// 该类表示一组字典项的只读聚合视图，包含分组标识、名称、可见性等元数据，以及
/// 该分组下的所有 `ImmutableDict` 列表。内部通过延迟构建的 `value -> ImmutableDict`
/// 索引，支持按字典值快速查找字典项，并提供私有/公开/受限等可见性判断方法。
@Value
@Builder
public class ImmutableDictGroup {

    Helper helper = new Helper();

    /// 字典分组的唯一标识符
    String id;
    /// 字典分组的名称
    String name;
    /// 字典分组的描述信息
    String description;
    /// 是否启用
    boolean enabled;
    /// 字典分组的可见性配置
    ///
    /// - `0`：私有
    /// - `1`：公开
    /// - `2`：受限
    int visibility;
    /// 是否预置
    boolean preset;
    /// 字典分组下的所有字典项的列表
    List<@NonNull ImmutableDict> dicts;

    /// 判断当前字典分组是否为私有。
    public boolean isPrivate() {
        return visibility == 0;
    }

    /// 判断当前字典分组是否为公开。
    public boolean isPublic() {
        return visibility == 1;
    }

    /// 判断当前字典分组是否为受限。
    public boolean isRestricted() {
        return visibility == 2;
    }

    /// 根据给定的值从字典分组中查找对应的字典项。
    /// 如果找不到对应的字典项，则抛出BizCodeException异常。
    ///
    /// @param value 字典项的值
    /// @return 找到的字典项
    /// @throws BizCodeException 如果找不到对应的字典项
    public ImmutableDict byValue(String value) {
        var b = helper.dictValueMap.get().get(value);
        if (b == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺失字典%s: %s", id, value);
        }
        return b;
    }

    private class Helper {

        LazyValue<Map<String, ImmutableDict>> dictValueMap = LazyValue.create(() -> {
            // 转换字典
            return dicts.stream().collect(ImmutableMap.toImmutableMap(ImmutableDict::getValue, Function.identity()));
        });
    }
}
