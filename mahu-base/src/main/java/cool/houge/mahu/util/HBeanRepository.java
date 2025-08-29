package cool.houge.mahu.util;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.domain.Pageable;
import cool.houge.mahu.domain.Sort;
import cool.houge.mahu.rsql.EBeanRSQLVisitor;
import cool.houge.mahu.rsql.ExtRSQLOperators;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.rsql.RSQLContext;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import io.ebean.BeanRepository;
import io.ebean.Database;
import io.ebean.typequery.QueryBean;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 扩展数据管理基类
///
/// @author ZY (kzou227@qq.com)
public class HBeanRepository<I, T> extends BeanRepository<I, T> {

    private static final Logger log = LogManager.getLogger(HBeanRepository.class);

    /// RSQL 解析器
    private static final RSQLParser RSQL_PARSER = new RSQLParser(ExtRSQLOperators.supportedOperators());

    protected HBeanRepository(Class<T> type, Database database) {
        super(type, database);
    }

    /// 获取指定ID的数据，如果数据不存在则抛出[BizCodes#NOT_FOUND]
    ///
    /// @param id 数据ID
    public @NonNull T loadById(@NonNull I id) throws BizCodeException {
        var bean = findById(id);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到指定的数据[" + id + "] " + type);
        }
        return bean;
    }

    /// 可搜索项
    protected @NonNull List<FilterItem> filterableItems() {
        return List.of();
    }

    /// 可排序项
    protected @NonNull List<FilterItem> sortableItems() {
        return filterableItems();
    }

    /// 将过滤条件应用到查询上
    ///
    /// @param query        查询对象
    /// @param filter       数据过滤条件
    /// @param page 分页参数
    protected final void apply(QueryBean<@NonNull T, ?> query, DataFilter filter, Pageable page) {
        this.apply(query, filter);
        this.apply(query, page);
    }

    /// 将过滤条件应用到查询上
    ///
    /// @param query        查询对象
    /// @param dataFilter       数据过滤条件
    protected final void apply(QueryBean<@NonNull T, ?> query, DataFilter dataFilter) {
        var filterItems = filterableItems();
        if (filterItems.isEmpty()) {
            throw new IllegalStateException("可过滤项为空");
        }

        // 包含软删除的数据
        if (dataFilter.isIncludeDeleted()) {
            query.setIncludeSoftDeletes();
        }

        dataFilter.query().filter(s -> !s.isEmpty()).ifPresent(q -> {
            try {
                var ctx = new RSQLContext(filterItems, query);
                var node = RSQL_PARSER.parse(q);
                node.accept(new EBeanRSQLVisitor(), ctx);
            } catch (RSQLParserException e) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "RSQL 解析错误", e);
            }
        });
    }

    /// 将分页参数应用到查询上
    ///
    /// @param query 查询对象
    /// @param page 分页参数
    protected final void apply(QueryBean<@NonNull T, ?> query, Pageable page) {
        if (page.isPaged()) {
            query.setFirstRow((int) page.getOffset()).setMaxRows(page.getPageSize());
        }
        this.apply(query, page.getSort());
    }

    protected final void apply(QueryBean<@NonNull T, ?> query, Sort sort) {
        if (sort.isUnsorted()) {
            return;
        }

        var items = sortableItems();
        if (items.isEmpty()) {
            throw new IllegalStateException("可排序项为空");
        }
        for (FilterItem item : items) {
            var o = sort.getOrderFor(item.getKey());
            if (o == null) {
                continue;
            }
            if (o.isAscending()) {
                query.query().orderBy().asc(item.getColumn());
            } else {
                query.query().orderBy().desc(item.getColumn());
            }
        }
    }
}
