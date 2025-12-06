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

/// 字典类型
///
/// 该类用于表示字典类型，包含字典类型的唯一标识符、名称、描述信息等属性。
/// 同时提供了一些方法来判断字典类型的可见性以及通过值获取字典项。
///
/// @author ZY (kzou227@qq.com)
@Value
@Builder
public class LcDictType {

    Helper helper = new Helper();

    /// 字典类型的唯一标识符
    int id;
    /// 字典类型的名称
    String name;
    /// 字典类型的描述信息
    String description;
    /// 字典类型是否被禁用
    boolean disabled;
    /// 字典类型的可见性配置
    ///
    /// - `0`：私有
    /// - `1`：公开
    /// - `2`：受限
    int visibility;
    /// 字典类型下的所有字典项的列表
    List<@NonNull LcDict> dicts;

    /// 判断当前字典类型是否为私有。
    public boolean isPrivate() {
        return visibility == 0;
    }

    /// 判断当前字典类型是否为公开。
    public boolean isPublic() {
        return visibility == 1;
    }

    /// 判断当前字典类型是否为受限。
    public boolean isRestricted() {
        return visibility == 2;
    }

    /// 根据给定的值从字典类型中查找对应的字典项。
    /// 如果找不到对应的字典项，则抛出BizCodeException异常。
    ///
    /// @param value 字典项的值
    /// @return 找到的字典项
    /// @throws BizCodeException 如果找不到对应的字典项
    public LcDict byValue(String value) {
        var b = helper.dictValueMap.get().get(value);
        if (b == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺失字典%s: %s", id, value);
        }
        return b;
    }

    private class Helper {

        LazyValue<Map<String, LcDict>> dictValueMap = LazyValue.create(() -> {
            // 转换字典
            return dicts.stream().collect(ImmutableMap.toImmutableMap(LcDict::getValue, Function.identity()));
        });
    }
}
