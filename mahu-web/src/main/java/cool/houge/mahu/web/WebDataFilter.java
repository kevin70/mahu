package cool.houge.mahu.web;

import static cool.houge.mahu.web.ServerRequestUtils.queryArg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.domain.Sort;
import io.ebean.PagedList;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.webserver.http.ServerRequest;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;

/// Web 数据过滤实现
///
/// @author ZY (kzou227@qq.com)
public class WebDataFilter implements DataFilter {

    private final OptionalValue<String> filter;
    private final Page page;
    private final boolean includeDeleted;

    public WebDataFilter(ServerRequest request) {
        this.filter = request.query().first("filter");
        this.page = Page.of(request.query());
        this.includeDeleted = queryArg(request, "include_deleted").asBoolean().orElse(false);
    }

    @Override
    public Optional<String> filter() {
        if (filter.isEmpty()) {
            return Optional.empty();
        }
        return filter.asOptional();
    }

    @Override
    public Page page() {
        return page;
    }

    @Override
    public Sort sort() {
        return page.getSort();
    }

    @Override
    public boolean includeDeleted() {
        return includeDeleted;
    }

    @Override
    public boolean noTotal() {
        return !page.isIncludeTotal();
    }

    @Override
    public @NonNull <I, T> Result<T> toResult(@NonNull PagedList<I> plist, @NonNull Function<I, T> mapping) {
        return new Result<>() {
            @Override
            public DataFilter getFilter() {
                return WebDataFilter.this;
            }

            @JsonProperty("total_items")
            @Override
            public int getTotalItems() {
                return plist.getTotalCount();
            }

            @Override
            public List<T> getItems() {
                return Lists.transform(plist.getList(), mapping::apply);
            }
        };
    }
}
