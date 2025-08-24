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

/// RSQL 过滤属性映射
///
/// @author ZY (kzou227@qq.com)
@Getter
@ToString
public class FilterItem {

    /// 过滤查询中的属性名称
    private final String key;
    /// 数据库列名或JPA属性名称
    private final String column;
    /// 值转换器
    @ToString.Exclude
    private final Function<String, ?> valueConverter;

    FilterItem(String key, String column, Function<String, ?> valueConverter) {
        requireNonNull(key, "key cannot be null");
        requireNonNull(column, "column cannot be null");
        requireNonNull(valueConverter, "valueConverter cannot be null");

        this.key = key;
        this.column = column;
        this.valueConverter = valueConverter;
    }

    public static FilterItem of(PBoolean<?> p) {
        return of(p, Boolean::valueOf);
    }

    public static FilterItem of(PInteger<?> p) {
        return of(p, Integer::valueOf);
    }

    public static FilterItem of(PLong<?> p) {
        return of(p, Long::valueOf);
    }

    public static FilterItem of(PFloat<?> p) {
        return of(p, Float::valueOf);
    }

    public static FilterItem of(PDouble<?> p) {
        return of(p, Double::valueOf);
    }

    public static FilterItem of(PBigDecimal<?> p) {
        return of(p, BigDecimal::new);
    }

    public static FilterItem of(PString<?> p) {
        return of(p, Function.identity());
    }

    public static FilterItem of(PLocalDate<?> p) {
        return of(p, LocalDate::parse);
    }

    public static FilterItem of(PLocalDateTime<?> p) {
        return of(p, LocalDateTime::parse);
    }

    public static FilterItem of(PZonedDateTime<?> p) {
        return of(p, ZonedDateTime::parse);
    }

    public static FilterItem of(POffsetDateTime<?> p) {
        return of(p, OffsetDateTime::parse);
    }

    public static FilterItem of(PInstant<?> p) {
        return of(p, InstantUtils::tryParse);
    }

    public static FilterItem of(String key, PBoolean<?> p) {
        return of(key, p, Boolean::valueOf);
    }

    public static FilterItem of(String key, PInteger<?> p) {
        return of(key, p, Integer::valueOf);
    }

    public static FilterItem of(String key, PLong<?> p) {
        return of(key, p, Long::valueOf);
    }

    public static FilterItem of(String key, PFloat<?> p) {
        return of(key, p, Float::valueOf);
    }

    public static FilterItem of(String key, PDouble<?> p) {
        return of(key, p, Double::valueOf);
    }

    public static FilterItem of(String key, PBigDecimal<?> p) {
        return of(key, p, BigDecimal::new);
    }

    public static FilterItem of(String key, PString<?> p) {
        return of(key, p, Function.identity());
    }

    public static FilterItem of(String key, PLocalDate<?> p) {
        return of(key, p, LocalDate::parse);
    }

    public static FilterItem of(String key, PLocalDateTime<?> p) {
        return of(key, p, LocalDateTime::parse);
    }

    public static FilterItem of(String key, PZonedDateTime<?> p) {
        return of(key, p, ZonedDateTime::parse);
    }

    public static FilterItem of(String key, POffsetDateTime<?> p) {
        return of(key, p, OffsetDateTime::parse);
    }

    public static FilterItem of(String key, PInstant<?> p) {
        return of(key, p, InstantUtils::tryParse);
    }

    public static FilterItem of(TQPropertyBase<?, ?> p, Function<String, ?> valueConverter) {
        var column = p.toString();
        var key = CamelCaseHelper.toUnderscoreFromCamel(column);
        return of(key, column, valueConverter);
    }

    public static FilterItem of(String key, TQPropertyBase<?, ?> p, Function<String, ?> valueConverter) {
        return of(key, p.toString(), valueConverter);
    }

    public static FilterItem of(String key, String column, Function<String, ?> valueConverter) {
        return new FilterItem(key, column, valueConverter);
    }
}
