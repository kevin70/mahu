package cool.houge.mahu.common.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.ebean.typequery.PInteger;
import io.ebean.typequery.TQPropertyBase;
import io.ebean.util.CamelCaseHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/// RSQL 过滤属性映射
///
/// @author ZY (kzou227@qq.com)
@Builder
@Getter
@RequiredArgsConstructor
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

    ///
    public static Builder of(PInteger<?> p) {
        return of(p, Integer::parseInt);
    }

    public static Builder of(TQPropertyBase<?, ?> p, Function<String, ?> valueConverter) {
        var columnName = p.toString();

        var b = new Builder();
        b.filterName(CamelCaseHelper.toUnderscoreFromCamel(columnName))
                .columnName(columnName)
                .valueConverter(valueConverter)
                .allowOperators(ALL);
        return b;
    }
}
