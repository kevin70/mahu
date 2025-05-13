package cool.houge.mahu.common;

import static cool.houge.mahu.common.rsql.RSQLOperators.BETWEEN;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.EQUAL;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.GREATER_THAN;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.GREATER_THAN_OR_EQUAL;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.LESS_THAN;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.LESS_THAN_OR_EQUAL;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.NOT_EQUAL;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.common.rsql.EBeanRSQLVisitor;
import cool.houge.mahu.common.rsql.FilterField;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.common.rsql.RSQLOperators;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import io.ebean.BeanRepository;
import io.ebean.Database;
import io.ebean.Query;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 扩展数据管理基类
///
/// @author ZY (kzou227@qq.com)
public class HBeanRepository<I, T> extends BeanRepository<I, T> {

    private static final Logger log = LogManager.getLogger(HBeanRepository.class);

    /// RSQL 解析器
    private static final RSQLParser RSQL_PARSER = new RSQLParser(RSQLOperators.supportedOperators());
    /// 常用的 created_at 属性过滤
    protected static final FilterField FF_CREATED_AT = FilterField.builder()
            .filterName("created_at")
            .columnName("createdAt")
            .valueConverter(LocalDateTime::parse)
            .allowOperators(List.of(
                    BETWEEN, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, EQUAL, NOT_EQUAL))
            .build();
    /// 常用的 updated_at 属性过滤
    protected static final FilterField FF_UPDATED_AT = FilterField.builder()
            .filterName("updated_at")
            .columnName("updatedAt")
            .valueConverter(LocalDateTime::parse)
            .allowOperators(List.of(
                    BETWEEN, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, EQUAL, NOT_EQUAL))
            .build();

    /// {@inheritDoc}
    protected HBeanRepository(Class<T> type, Database database) {
        super(type, database);
    }

    /// 将过滤条件应用到查询上
    ///
    /// @param filter 数据过滤条件
    /// @param filterFields 过滤字段
    /// @param query 查询对象
    protected void apply(DataFilter filter, List<FilterField> filterFields, Query<?> query) {
        var ctx = new RSQLContext(filterFields, query);
        if (filter.filter() != null && !filter.filter().isEmpty()) {
            try {
                var node = RSQL_PARSER.parse(filter.filter());
                node.accept(new EBeanRSQLVisitor(), ctx);
            } catch (RSQLParserException e) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "RSQL解析错误", e);
            }
        }

        this.applyPage(filter, query);
        this.applySort(filter, query, ctx);

        // 包含软删除的数据
        if (filter.isIncludeDeleted()) {
            query.setIncludeSoftDeletes();
        }
    }

    void applyPage(DataFilter filter, Query<?> query) {
        if (filter.offset() > 0) {
            query.setFirstRow(filter.offset());
        }

        if (filter.limit() > 0) {
            query.setMaxRows(filter.limit());
        }
    }

    void applySort(DataFilter filter, Query<?> query, RSQLContext ctx) {
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
                    query.orderBy().asc(field.getColumnName());
                } else {
                    query.orderBy().desc(field.getColumnName());
                }
            }
        }
    }
}
