package cool.houge.mahu.common;

import cool.houge.mahu.common.rsql.EBeanRSQLVisitor;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.common.rsql.RSQLOperators;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import io.ebean.BeanRepository;
import io.ebean.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 扩展数据管理基类
///
/// @author ZY (kzou227@qq.com)
public class HBeanRepository<I, T> extends BeanRepository<I, T> {

    private static final Logger log = LogManager.getLogger(HBeanRepository.class);

    /// RSQL 解析器
    private static final RSQLParser RSQL_PARSER = new RSQLParser(RSQLOperators.supportedOperators());

    /// {@inheritDoc}
    protected HBeanRepository(Class<T> type, Database database) {
        super(type, database);
    }

    /// 将过滤条件应用到查询上
    ///
    /// @param filter 数据过滤条件
    /// @param ctx RSQL 上下文
    protected void apply(DataFilter filter, RSQLContext ctx) {
        if (filter.filter() != null && !filter.filter().isEmpty()) {
            try {
                var node = RSQL_PARSER.parse(filter.filter());
                node.accept(new EBeanRSQLVisitor(), ctx);
            } catch (RSQLParserException e) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "错误RSQL过滤条件");
            }
        }

        if (filter.offset() > 0) {
            ctx.queryBean().setFirstRow(filter.offset());
        }

        if (filter.limit() > 0) {
            ctx.queryBean().setMaxRows(filter.limit());
        }

        // 包含软删除的数据
        if (filter.isIncludeDeleted()) {
            ctx.queryBean().setIncludeSoftDeletes();
        }

        // 排序
        var sorts = filter.sorts();
        for (String s : sorts) {
            if (s == null || s.isEmpty()) {
                continue;
            }

            boolean ascending = s.charAt(0) != '-';
            String name = ascending ? s : s.substring(1);
            var property = ctx.getProperty(name);
            if (property == null) {
                log.debug("不存在的排序属性 {}", name);
                continue;
            }

            if (ascending) {
                property.original().asc();
            } else {
                property.original().desc();
            }
        }
    }
}
