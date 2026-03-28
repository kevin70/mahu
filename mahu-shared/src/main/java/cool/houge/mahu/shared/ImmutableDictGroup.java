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

/// 不可变的字典分组对象
///
/// 通过 Caffeine 缓存管理字典分组及其包含的所有字典数据，使用不可变对象确保线程安全。
/// 支持按可见性快速判断、按值查询字典等常用操作。
///
/// @author ZY (kzou227@qq.com)
@Value
@Builder
public class ImmutableDictGroup {

    Helper helper = new Helper();

    /// 字典分组 ID（主键）
    String id;
    /// 字典分组名称
    String name;
    /// 字典分组描述
    String description;
    /// 是否启用
    boolean enabled;
    /// 可见性（0=私有、1=公开、2=受限）
    int visibility;
    /// 是否为预置字典分组
    boolean preset;
    /// 该分组包含的所有字典数据
    List<@NonNull ImmutableDict> dicts;

    /// 判断是否为私有字典分组
    /// @return true 表示私有（仅内部使用）
    public boolean isPrivate() {
        return visibility == 0;
    }

    /// 判断是否为公开字典分组
    /// @return true 表示公开（任何人可访问）
    public boolean isPublic() {
        return visibility == 1;
    }

    /// 判断是否为受限字典分组
    /// @return true 表示受限（特定权限可访问）
    public boolean isRestricted() {
        return visibility == 2;
    }

    /// 根据字典值快速查询对应的字典对象
    ///
    /// @param value 字典值
    /// @return 对应的字典对象
    /// @throws BizCodeException 当字典值不存在时抛出异常
    public ImmutableDict byValue(String value) {
        var b = helper.dictValueMap.get().get(value);
        if (b == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺失字典%s: %s", id, value);
        }
        return b;
    }

    /// 字典值到字典对象的映射表（延迟初始化）
    private class Helper {
        LazyValue<Map<String, ImmutableDict>> dictValueMap = LazyValue.create(() -> {
            return dicts.stream().collect(ImmutableMap.toImmutableMap(ImmutableDict::getValue, Function.identity()));
        });
    }
}
