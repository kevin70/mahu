package cool.houge.mahu.common.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.ebean.typequery.*;
import io.ebean.util.CamelCaseHelper;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/// RSQL 过滤属性映射
///
/// @author ZY (kzou227@qq.com)
@Builder
@Getter
public class FilterField {

    /// 支持所有操作符
    public static final List<ComparisonOperator> ALL = new ArrayList<>();

    /// 过滤查询中的属性名称
    private final String filterName;
    /// 数据库列名或JPA属性名称
    private final String columnName;
    /// 值转换器
    private final Function<String, ?> valueConverter;
    /// 可接受的查询操作符
    private final List<ComparisonOperator> allowOperators;

    private FilterField(
            String filterName,
            String columnName,
            Function<String, ?> valueConverter,
            List<ComparisonOperator> allowOperators) {
        requireNonNull(filterName, "filterName cannot be null");
        requireNonNull(columnName, "columnName cannot be null");
        requireNonNull(valueConverter, "valueConverter cannot be null");
        requireNonNull(allowOperators, "allowOperators cannot be null");

        this.filterName = filterName;
        this.columnName = columnName;
        this.valueConverter = valueConverter;
        this.allowOperators = allowOperators;
    }

    /// 是否可接受指定的操作符
    public boolean isAllowOperator(ComparisonOperator operator) {
        if (allowOperators == ALL) {
            return true;
        }
        return allowOperators.contains(operator);
    }

    public static Builder with(PBoolean<?> p) {
        return with(p, Boolean::parseBoolean);
    }

    public static Builder with(PInteger<?> p) {
        return with(p, Integer::parseInt);
    }

    public static Builder with(PLong<?> p) {
        return with(p, Long::parseLong);
    }

    public static Builder with(PDouble<?> p) {
        return with(p, Double::parseDouble);
    }

    public static Builder with(PLocalDate<?> p) {
        return with(p, LocalDate::parse);
    }

    public static Builder with(PLocalDateTime<?> p) {
        return with(p, LocalDateTime::parse);
    }

    public static Builder with(PString<?> p) {
        return with(p, Function.identity());
    }

    public static Builder with(TQPropertyBase<?, ?> p, Function<String, ?> valueConverter) {
        var columnName = p.toString();
        return builder()
                .filterName(CamelCaseHelper.toUnderscoreFromCamel(columnName))
                .columnName(columnName)
                .valueConverter(valueConverter)
                .allowOperators(ALL);
    }
}
