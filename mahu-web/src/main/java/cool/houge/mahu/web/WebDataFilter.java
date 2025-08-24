package cool.houge.mahu.web;

import static cool.houge.mahu.web.ServerRequestUtils.queryArg;

import cool.houge.mahu.util.DataFilter;
import io.helidon.webserver.http.ServerRequest;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// Web 数据过滤实现
///
/// @author ZY (kzou227@qq.com)
public class WebDataFilter implements DataFilter {

    private final boolean hasPage;
    private final int offset;
    private final int limit;

    private final List<String> sorts;
    private final String filter;
    private final boolean includeDeleted;
    private final boolean noTotalCount;

    public WebDataFilter(ServerRequest request) {
        this.hasPage = request.query().contains("limit");
        if (hasPage) {
            this.offset = queryArg(request,"offset").asInt().orElse(0);
            this.limit = queryArg(request, "limit").asInt().get();
        } else  {
            this.offset = -1;
            this.limit = -1;
        }
        this.sorts = request.query().all("sort", List::of);
        this.filter = request.query().first("filter").orElse(null);
        this.includeDeleted = queryArg(request, "include_deleted").asBoolean().orElse(false);
        this.noTotalCount = queryArg(request, "no_total_count").asBoolean().orElse(false);
    }

    @Override
    public boolean hasPage() {
        return hasPage;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public int limit() {
        return limit;
    }

    @Override
    public @NonNull List<String> sorts() {
        return sorts;
    }

    @Override
    public String filter() {
        return filter;
    }

    @Override
    public boolean isIncludeDeleted() {
        return includeDeleted;
    }

    @Override
    public boolean isNoTotalCount() {
        return noTotalCount;
    }
}
