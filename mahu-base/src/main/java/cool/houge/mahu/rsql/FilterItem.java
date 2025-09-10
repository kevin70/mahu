package cool.houge.mahu.rsql;

import static java.util.Objects.requireNonNull;

import cool.houge.mahu.util.InstantUtils;
import io.ebean.typequery.PBigDecimal;
import io.ebean.typequery.PBoolean;
import io.ebean.typequery.PDouble;
import io.ebean.typequery.PFloat;
import io.ebean.typequery.PInstant;
import io.ebean.typequery.PInteger;
import io.ebean.typequery.PLocalDate;
import io.ebean.typequery.PLocalDateTime;
import io.ebean.typequery.PLong;
import io.ebean.typequery.POffsetDateTime;
import io.ebean.typequery.PString;
import io.ebean.typequery.PZonedDateTime;
import io.ebean.typequery.TQPropertyBase;
import io.ebean.util.CamelCaseHelper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;

/// RSQL 过滤属性映射类，用于建立前端查询参数与后端数据存储之间的映射关系
///
/// 该类负责管理三个核心信息：
/// 1. 前端过滤查询中使用的属性名称（`key`）
/// 2. 后端对应的数据库列名或JPA属性名称（`column`）
/// 3. 将前端字符串值转换为后端所需类型的转换器（`valueConverter`）
///
/// @author ZY (kzou227@qq.com)
@Getter
@ToString
public class FilterItem {

    /// 过滤查询中的前端属性名称，对应`URL`参数中的键名
    private final String key;

    /// 后端对应的数据库列名或`JPA/EBean`属性名称
    private final String column;

    /// 值转换器，用于将前端传递的字符串值转换为后端所需的数据类型
    @ToString.Exclude
    private final Function<String, ?> valueConverter;

    /// 构造函数，创建`FilterItem`实例
    ///
    /// @param key 前端过滤属性名称，不可为`null`
    /// @param column 后端数据库列名或属性名，不可为`null`
    /// @param valueConverter 值转换器函数，不可为`null`
    /// @throws NullPointerException 如果任何参数为`null`时抛出
    FilterItem(String key, String column, Function<String, ?> valueConverter) {
        requireNonNull(key, "key cannot be null");
        requireNonNull(column, "column cannot be null");
        requireNonNull(valueConverter, "valueConverter cannot be null");

        this.key = key;
        this.column = column;
        this.valueConverter = valueConverter;
    }

    /// 创建布尔类型的`FilterItem`
    ///
    /// @param p 布尔类型属性标识
    /// @return 新的`FilterItem`实例，使用`Boolean::valueOf`作为转换器
    public static FilterItem of(PBoolean<?> p) {
        return of(p, Boolean::valueOf);
    }

    /// 创建整数类型的`FilterItem`
    ///
    /// @param p 整数类型属性标识
    /// @return 新的`FilterItem`实例，使用`Integer::valueOf`作为转换器
    public static FilterItem of(PInteger<?> p) {
        return of(p, Integer::valueOf);
    }

    /// 创建长整数类型的`FilterItem`
    ///
    /// @param p 长整数类型属性标识
    /// @return 新的`FilterItem`实例，使用`Long::valueOf`作为转换器
    public static FilterItem of(PLong<?> p) {
        return of(p, Long::valueOf);
    }

    /// 创建单精度浮点类型的`FilterItem`
    ///
    /// @param p 单精度浮点类型属性标识
    /// @return 新的`FilterItem`实例，使用`Float::valueOf`作为转换器
    public static FilterItem of(PFloat<?> p) {
        return of(p, Float::valueOf);
    }

    /// 创建双精度浮点类型的`FilterItem`
    ///
    /// @param p 双精度浮点类型属性标识
    /// @return 新的`FilterItem`实例，使用`Double::valueOf`作为转换器
    public static FilterItem of(PDouble<?> p) {
        return of(p, Double::valueOf);
    }

    /// 创建`BigDecimal`类型的`FilterItem`
    ///
    /// @param p `BigDecimal`类型属性标识
    /// @return 新的`FilterItem`实例，使用`BigDecimal::new`作为转换器
    public static FilterItem of(PBigDecimal<?> p) {
        return of(p, BigDecimal::new);
    }

    /// 创建字符串类型的`FilterItem`
    ///
    /// @param p 字符串类型属性标识
    /// @return 新的`FilterItem`实例，使用`Function.identity()`作为转换器（不做转换）
    public static FilterItem of(PString<?> p) {
        return of(p, Function.identity());
    }

    /// 创建`LocalDate`类型的`FilterItem`
    ///
    /// @param p `LocalDate`类型属性标识
    /// @return 新的`FilterItem`实例，使用`LocalDate::parse`作为转换器
    public static FilterItem of(PLocalDate<?> p) {
        return of(p, LocalDate::parse);
    }

    /// 创建`LocalDateTime`类型的`FilterItem`
    ///
    /// @param p `LocalDateTime`类型属性标识
    /// @return 新的`FilterItem`实例，使用`LocalDateTime::parse`作为转换器
    public static FilterItem of(PLocalDateTime<?> p) {
        return of(p, LocalDateTime::parse);
    }

    /// 创建`ZonedDateTime`类型的`FilterItem`
    ///
    /// @param p `ZonedDateTime`类型属性标识
    /// @return 新的`FilterItem`实例，使用`ZonedDateTime::parse`作为转换器
    public static FilterItem of(PZonedDateTime<?> p) {
        return of(p, ZonedDateTime::parse);
    }

    /// 创建`OffsetDateTime`类型的`FilterItem`
    ///
    /// @param p `OffsetDateTime`类型属性标识
    /// @return 新的`FilterItem`实例，使用`OffsetDateTime::parse`作为转换器
    public static FilterItem of(POffsetDateTime<?> p) {
        return of(p, OffsetDateTime::parse);
    }

    /// 创建`Instant`类型的`FilterItem`
    ///
    /// @param p `Instant`类型属性标识
    /// @return 新的`FilterItem`实例，使用`InstantUtils::tryParse`作为转换器
    public static FilterItem of(PInstant<?> p) {
        return of(p, InstantUtils::tryParse);
    }

    /// 使用自定义`key`创建布尔类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p 布尔类型属性标识
    /// @return 新的`FilterItem`实例，使用`Boolean::valueOf`作为转换器
    public static FilterItem of(String key, PBoolean<?> p) {
        return of(key, p, Boolean::valueOf);
    }

    /// 使用自定义`key`创建整数类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p 整数类型属性标识
    /// @return 新的`FilterItem`实例，使用`Integer::valueOf`作为转换器
    public static FilterItem of(String key, PInteger<?> p) {
        return of(key, p, Integer::valueOf);
    }

    /// 使用自定义`key`创建长整数类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p 长整数类型属性标识
    /// @return 新的`FilterItem`实例，使用`Long::valueOf`作为转换器
    public static FilterItem of(String key, PLong<?> p) {
        return of(key, p, Long::valueOf);
    }

    /// 使用自定义`key`创建单精度浮点类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p 单精度浮点类型属性标识
    /// @return 新的`FilterItem`实例，使用`Float::valueOf`作为转换器
    public static FilterItem of(String key, PFloat<?> p) {
        return of(key, p, Float::valueOf);
    }

    /// 使用自定义`key`创建双精度浮点类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p 双精度浮点类型属性标识
    /// @return 新的`FilterItem`实例，使用`Double::valueOf`作为转换器
    public static FilterItem of(String key, PDouble<?> p) {
        return of(key, p, Double::valueOf);
    }

    /// 使用自定义`key`创建`BigDecimal`类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p `BigDecimal`类型属性标识
    /// @return 新的`FilterItem`实例，使用`BigDecimal::new`作为转换器
    public static FilterItem of(String key, PBigDecimal<?> p) {
        return of(key, p, BigDecimal::new);
    }

    /// 使用自定义`key`创建字符串类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p 字符串类型属性标识
    /// @return 新的`FilterItem`实例，使用`Function.identity()`作为转换器
    public static FilterItem of(String key, PString<?> p) {
        return of(key, p, Function.identity());
    }

    /// 使用自定义`key`创建`LocalDate`类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p `LocalDate`类型属性标识
    /// @return 新的`FilterItem`实例，使用`LocalDate::parse`作为转换器
    public static FilterItem of(String key, PLocalDate<?> p) {
        return of(key, p, LocalDate::parse);
    }

    /// 使用自定义`key`创建`LocalDateTime`类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p `LocalDateTime`类型属性标识
    /// @return 新的`FilterItem`实例，使用`LocalDateTime::parse`作为转换器
    public static FilterItem of(String key, PLocalDateTime<?> p) {
        return of(key, p, LocalDateTime::parse);
    }

    /// 使用自定义`key`创建`ZonedDateTime`类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p `ZonedDateTime`类型属性标识
    /// @return 新的`FilterItem`实例，使用`ZonedDateTime::parse`作为转换器
    public static FilterItem of(String key, PZonedDateTime<?> p) {
        return of(key, p, ZonedDateTime::parse);
    }

    /// 使用自定义`key`创建`OffsetDateTime`类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p `OffsetDateTime`类型属性标识
    /// @return 新的`FilterItem`实例，使用`OffsetDateTime::parse`作为转换器
    public static FilterItem of(String key, POffsetDateTime<?> p) {
        return of(key, p, OffsetDateTime::parse);
    }

    /// 使用自定义`key`创建`Instant`类型的`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p `Instant`类型属性标识
    /// @return 新的`FilterItem`实例，使用`InstantUtils::tryParse`作为转换器
    public static FilterItem of(String key, PInstant<?> p) {
        return of(key, p, InstantUtils::tryParse);
    }

    /// 基于`Querydsl`属性创建`FilterItem`
    ///
    /// `key`会自动通过`CamelCaseHelper`转换为下划线命名，
    /// `column`使用`Querydsl`属性的字符串表示
    ///
    /// @param p `Querydsl`属性对象（`TQPropertyBase`）
    /// @param valueConverter 值转换器函数
    /// @return 新的`FilterItem`实例
    public static FilterItem of(TQPropertyBase<?, ?> p, Function<String, ?> valueConverter) {
        var column = p.toString();
        var key = CamelCaseHelper.toUnderscoreFromCamel(column);
        return of(key, column, valueConverter);
    }

    /// 使用自定义`key`和`Querydsl`属性创建`FilterItem`
    ///
    /// @param key 自定义前端属性名称
    /// @param p `Querydsl`属性对象（`TQPropertyBase`）
    /// @param valueConverter 值转换器函数
    /// @return 新的`FilterItem`实例
    public static FilterItem of(String key, TQPropertyBase<?, ?> p, Function<String, ?> valueConverter) {
        return of(key, p.toString(), valueConverter);
    }

    /// 完全自定义创建`FilterItem`
    ///
    /// @param key 前端过滤属性名称
    /// @param column 后端数据库列名或属性名
    /// @param valueConverter 值转换器函数
    /// @return 新的`FilterItem`实例
    public static FilterItem of(String key, String column, Function<String, ?> valueConverter) {
        return new FilterItem(key, column, valueConverter);
    }
}
