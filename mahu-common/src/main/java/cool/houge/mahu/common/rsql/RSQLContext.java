package cool.houge.mahu.common.rsql;

import static java.util.Objects.requireNonNull;

import io.ebean.typequery.QueryBean;
import java.util.List;
import java.util.Objects;

/// RSQL 查询上下文
///
/// @author ZY (kzou227@qq.com)
public class RSQLContext {

    private final List<FilterField> filterFields;
    private final QueryBean<?, ?> query;

    public RSQLContext(List<FilterField> filterFields, QueryBean<?, ?> query) {
        requireNonNull(filterFields);
        requireNonNull(query);
        this.filterFields = filterFields;
        this.query = query;
    }

    public QueryBean<?, ?> query() {
        return query;
    }

    public FilterField getFilterField(String fieldName) {
        for (FilterField filterField : filterFields) {
            if (Objects.equals(filterField.getFilterName(), fieldName)) {
                return filterField;
            }
        }
        return null;
    }
}
