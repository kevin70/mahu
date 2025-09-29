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
import io.ebean.Query;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// 扩展数据管理基类
///
/// @author ZY (kzou227@qq.com)
public class HBeanRepository<I, T> extends BeanRepository<@NonNull I, @NonNull T> {

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

    /// 将过滤条件应用到查询上
    ///
    /// @param query        查询对象
    /// @param dataFilter       数据过滤条件
    /// @param filterItems 可过滤项
    protected final void apply(Query<T> query, DataFilter dataFilter, List<FilterItem> filterItems) {
        this.apply(query, dataFilter, filterItems, filterItems);
    }

    /// 将过滤条件应用到查询上
    ///
    /// @param query        查询对象
    /// @param dataFilter       数据过滤条件
    /// @param filterItems 可过滤项
    /// @param sortItems 可排序项
    protected final void apply(
            Query<T> query, DataFilter dataFilter, List<FilterItem> filterItems, List<FilterItem> sortItems) {
        // 包含软删除的数据
        if (dataFilter.includeDeleted()) {
            query.setIncludeSoftDeletes();
        }

        if (dataFilter.query().isPresent() && !filterItems.isEmpty()) {
            this.applyFilter(query, dataFilter.query().get(), filterItems);
        }

        if (sortItems.isEmpty()) {
            if (!dataFilter.page().isUnpaged()) {
                this.applyPage(query, dataFilter.page());
            }
        } else {
            if (dataFilter.page().isUnpaged()) {
                this.applySort(query, dataFilter.page().getSort(), sortItems);
            } else {
                this.applyPage(query, dataFilter.page(), sortItems);
            }
        }
    }

    /// 将分页参数应用到查询上
    ///
    /// @param query 查询对象
    /// @param filter RSQL 过滤查询
    /// @param filterItems 可过滤项
    /// @return this
    protected final HBeanRepository<I, T> applyFilter(Query<T> query, String filter, List<FilterItem> filterItems) {
        if (filterItems == null || filterItems.isEmpty()) {
            throw new IllegalArgumentException("filterItems 不能为空");
        }

        if (filter != null && !filter.isEmpty()) {
            try {
                var ctx = new RSQLContext(filterItems, query);
                var node = RSQL_PARSER.parse(filter);
                node.accept(new EBeanRSQLVisitor(), ctx);
            } catch (RSQLParserException e) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "RSQL 解析错误", e);
            }
        }
        return this;
    }

    /// 将分页参数应用到查询上
    ///
    /// @param query 查询对象
    /// @param page 分页参数
    /// @param sortItems 可排序项
    /// @return this
    protected final HBeanRepository<I, T> applyPage(Query<T> query, Pageable page, List<FilterItem> sortItems) {
        return applyPage(query, page).applySort(query, page.getSort(), sortItems);
    }

    /// 将分页参数应用到查询上
    ///
    /// @param query 查询对象
    /// @param page 分页参数
    /// @return this
    protected final HBeanRepository<I, T> applyPage(Query<T> query, Pageable page) {
        query.setFirstRow((int) page.getOffset()).setMaxRows(page.getPageSize());
        return this;
    }

    /// 将排序参数应用到查询上
    ///
    /// @param query     查询对象
    /// @param sort      排序参数
    /// @param sortItems 可排序项
    /// @return this
    protected final HBeanRepository<I, T> applySort(Query<T> query, Sort sort, List<FilterItem> sortItems) {
        if (sortItems == null || sortItems.isEmpty()) {
            throw new IllegalArgumentException("sortItems 不能为空");
        }
        if (sort.isSorted()) {
            for (FilterItem item : sortItems) {
                var o = sort.getOrderFor(item.getKey());
                if (o == null) {
                    continue;
                }
                if (o.getDirection() == Sort.Direction.ASC) {
                    query.orderBy().asc(item.getColumn());
                } else {
                    query.orderBy().desc(item.getColumn());
                }
            }
        }
        return this;
    }
}
