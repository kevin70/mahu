package cool.houge.mahu.rsql;

import static java.util.Objects.requireNonNull;

import io.ebean.typequery.QueryBean;
import java.util.List;
import java.util.Objects;

/// RSQL 查询上下文
///
/// @author ZY (kzou227@qq.com)
public class RSQLContext {

    private final List<FilterItem> filterItems;
    private final QueryBean<?, ?> query;

    public RSQLContext(List<FilterItem> filterItems, QueryBean<?, ?> query) {
        requireNonNull(filterItems);
        requireNonNull(query);
        this.filterItems = filterItems;
        this.query = query;
    }

    @SuppressWarnings({"rawtypes"})
    public QueryBean query() {
        return query;
    }

    public FilterItem getFilterField(String key) {
        for (FilterItem filterItem : filterItems) {
            if (Objects.equals(filterItem.getKey(), key)) {
                return filterItem;
            }
        }
        return null;
    }
}
