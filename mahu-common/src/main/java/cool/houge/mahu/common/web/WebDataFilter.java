package cool.houge.mahu.common.web;

import com.google.common.primitives.UnsignedInts;
import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import cool.houge.mahu.common.DataFilter;
import io.helidon.webserver.http.ServerRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

/// Web 数据过滤实现
///
/// @author ZY (kzou227@qq.com)
public class WebDataFilter implements DataFilter {

    private final int offset;
    private final int limit;
    private final List<String> sorts;
    private final String filter;
    private final String pageToken;

    private final boolean hasPage;
    private final boolean includeDeleted;
    private final boolean noTotalCount;

    public WebDataFilter(ServerRequest request) {
        var query = request.query();
        this.offset = query.first("offset")
                .map(v -> {
                    try {
                        return UnsignedInts.parseUnsignedInt(v);
                    } catch (NumberFormatException e) {
                        throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "非法的参数[offset=" + v + "]");
                    }
                })
                .orElse(0);
        this.limit = query.first("limit")
                .map(v -> {
                    try {
                        return UnsignedInts.parseUnsignedInt(v);
                    } catch (NumberFormatException e) {
                        throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "非法的参数[limit=" + v + "]");
                    }
                })
                .orElse(DEFAULT_LIMIT);

        if (query.contains("sort")) {
            this.sorts = query.all("sort");
        } else {
            this.sorts = List.of();
        }

        this.filter = query.first("filter").orElse(null);
        this.pageToken = query.first("page_token").orElse(null);
        this.hasPage = query.contains("limit");
        this.includeDeleted =
                query.first("include_deleted").map(v -> Objects.equals(v, "1")).orElse(false);
        this.noTotalCount =
                query.first("no_total_count").map(v -> Objects.equals(v, "1")).orElse(false);
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
    public String pageToken() {
        return pageToken;
    }

    @Override
    public boolean hasPage() {
        return hasPage;
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
