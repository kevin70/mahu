package cool.houge.mahu.common.web;

import com.google.common.base.Splitter;
import com.google.common.primitives.UnsignedInts;
import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import cool.houge.mahu.common.DataFilter;
import io.helidon.webserver.http.ServerRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// Web 数据过滤实现
///
/// @author ZY (kzou227@qq.com)
public class WebDataFilter implements DataFilter {

    private static final Splitter SPACE_SPLITTER = Splitter.on(' ').trimResults();

    private final int offset;
    private final int limit;
    private final List<Sort> sorts;
    private final String filter;
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
            this.sorts = parseSorts(query.all("sort"));
        } else {
            this.sorts = List.of();
        }

        this.filter = query.first("filter").orElse("");

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
    public @NonNull List<Sort> sorts() {
        return sorts;
    }

    @Override
    public @NonNull List<Filter> filters() {
        return List.of();
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

    List<Sort> parseSorts(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return List.of();
        }

        var list = new ArrayList<Sort>(strings.size());
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                continue;
            }

            boolean ascending = s.charAt(0) != '-';
            String name = ascending ? s : s.substring(1);
            list.add(new Sort(name, ascending ? Direction.asc : Direction.desc));
        }
        return list;
    }

    List<Filter> parseFilters(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return List.of();
        }

        var rs = new ArrayList<Filter>(strings.size());
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                continue;
            }

            var arrs = SPACE_SPLITTER.splitToList(s);
            rs.add(new Filter(arrs.getFirst(), Op.valueOf(arrs.get(1)), arrs.get(2)));
        }
        return rs;
    }
}
