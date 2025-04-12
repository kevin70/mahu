package cool.houge.mahu.common.rsql;

import io.ebean.Query;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/// RSQL 查询上下文
///
/// @author ZY (kzou227@qq.com)
public class RSQLContext {

    private final List<FilterField> filterFields;
    private final Query<?> query;

    public RSQLContext(List<FilterField> filterFields, Query<?> query) {
        requireNonNull(filterFields);
        requireNonNull(query);
        this.filterFields = filterFields;
        this.query = query;
    }

    public Query<?> query() {
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
