package cool.houge.mahu.common.rsql;

import static java.util.Objects.requireNonNull;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.ebean.typequery.PBoolean;
import io.ebean.typequery.PDouble;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;

/// RSQL 过滤属性映射
///
/// @author ZY (kzou227@qq.com)
@lombok.Builder
@Getter
@ToString
public class FilterField {

    /// 支持所有操作符
    public static final List<ComparisonOperator> ALL = Collections.unmodifiableList(new ArrayList<>());

    /// 过滤查询中的属性名称
    private final String filterName;
    /// 数据库列名或JPA属性名称
    private final String columnName;
    /// 值转换器
    @ToString.Exclude
    private final Function<String, ?> valueConverter;
    /// 可接受的查询操作符
    private final List<ComparisonOperator> allowOperators;

    FilterField(
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

    public static class Builder {

        public Builder with(PBoolean<?> p) {
            return with(p, Boolean::parseBoolean);
        }

        public Builder with(PInteger<?> p) {
            return with(p, Integer::parseInt);
        }

        public Builder with(PLong<?> p) {
            return with(p, Long::parseLong);
        }

        public Builder with(PDouble<?> p) {
            return with(p, Double::parseDouble);
        }

        public Builder with(PInstant<?> p) {
            return with(p, LocalDate::parse);
        }

        public Builder with(PLocalDate<?> p) {
            return with(p, LocalDate::parse);
        }

        public Builder with(PLocalDateTime<?> p) {
            return with(p, LocalDateTime::parse);
        }

        public Builder with(PZonedDateTime<?> p) {
            return with(p, ZonedDateTime::parse);
        }

        public Builder with(POffsetDateTime<?> p) {
            return with(p, OffsetDateTime::parse);
        }

        public Builder with(PString<?> p) {
            return with(p, Function.identity());
        }

        public Builder with(TQPropertyBase<?, ?> p, Function<String, ?> valueConverter) {
            var columnName = p.toString();
            return builder()
                    .filterName(CamelCaseHelper.toUnderscoreFromCamel(columnName))
                    .columnName(columnName)
                    .valueConverter(valueConverter)
                    .allowOperators(ALL);
        }
    }
}
