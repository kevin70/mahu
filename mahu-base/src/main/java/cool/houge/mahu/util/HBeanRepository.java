package cool.houge.mahu.util;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
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
    public @NonNull T obtainById(@NonNull I id) throws BizCodeException {
        var bean = findById(id);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到指定的数据[" + id + "] " + type);
        }
        return bean;
    }

    /// 将过滤条件应用到查询上
    ///
    /// @param query        查询对象
    /// @param filter       数据过滤条件
    /// @param filterItems 过滤字段
    protected void apply(QueryBean<?, ?> query, DataFilter filter, List<FilterItem> filterItems) {
        var ctx = new RSQLContext(filterItems, query);
        if (filter.filter() != null && !filter.filter().isEmpty()) {
            try {
                var node = RSQL_PARSER.parse(filter.filter());
                node.accept(new EBeanRSQLVisitor(), ctx);
            } catch (RSQLParserException e) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "RSQL解析错误", e);
            }
        }

        this.applyPaging(query, filter);
        this.applySort(query, filter, ctx);

        // 包含软删除的数据
        if (filter.isIncludeDeleted()) {
            query.setIncludeSoftDeletes();
        }
    }

    /// 将分页参数应用到查询上
    ///
    /// @param query 查询对象
    /// @param page 分页参数
    protected void apply(QueryBean<?, ?> query, Pageable page) {
        this.apply(query, page, List.of());
    }

    protected void apply(QueryBean<?, ?> query, Pageable page, List<FilterItem> sortItems) {
        if (page.isPaged()) {
            query.setFirstRow((int) page.getOffset()).setMaxRows(page.getPageSize());
        }
        this.apply(query, page.getSort(), sortItems);
    }

    protected void apply(QueryBean<?, ?> query, Sort sort, List<FilterItem> items) {
        if (sort.isUnsorted()) {
            return;
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

    /// 将分页参数应用到查询上
    ///
    /// @param query 查询对象
    /// @param paging 分页参数
    protected void applyPaging(QueryBean<?, ?> query, Paging paging) {
        if (paging.offset() > 0) {
            query.setFirstRow(paging.offset());
        }

        if (paging.limit() > 0) {
            query.setMaxRows(paging.limit());
        }
    }

    void applySort(QueryBean<?, ?> query, DataFilter filter, RSQLContext ctx) {
        for (String s : filter.sorts()) {
            if (s == null || s.isEmpty()) {
                continue;
            }

            boolean ascending = s.charAt(0) != '-';
            String name = ascending ? s : s.substring(1);
            var field = ctx.getFilterField(name);
            if (field != null) {
                log.debug("找到排序属性 {}: {}", name, field);
                if (ascending) {
                    query.query().orderBy().asc(field.getColumn());
                } else {
                    query.query().orderBy().desc(field.getColumn());
                }
            }
        }
    }
}
