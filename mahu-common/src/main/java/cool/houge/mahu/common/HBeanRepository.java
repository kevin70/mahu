package cool.houge.mahu.common;

import cool.houge.mahu.common.rsql.EBeanRSQLVisitor;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.common.rsql.RSQLOperators;
import cz.jirutka.rsql.parser.RSQLParser;
import io.ebean.*;
import io.ebean.plugin.BeanType;
import io.ebean.util.CamelCaseHelper;
import io.helidon.common.LazyValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/// 扩展数据管理基类
///
/// @author ZY (kzou227@qq.com)
public class HBeanRepository<I, T> extends BeanRepository<I, T> {

    private static final Logger log = LogManager.getLogger(HBeanRepository.class);

    private final LazyValue<BeanType<T>> beanTypeLv =
            LazyValue.create(() -> db().pluginApi().beanType(super.type));
    /// RSQL 解析器
    private static final RSQLParser RSQL_PARSER = new RSQLParser(RSQLOperators.supportedOperators());

    /// {@inheritDoc}
    protected HBeanRepository(Class<T> type, Database database) {
        super(type, database);
    }

    /// 将过滤条件应用到查询上
    ///
    /// @param query  查询对象
    /// @param filter 数据过滤条件
    protected void apply(Query<T> query, DataFilter filter) {
        var sorts = filter.sorts();
        for (DataFilter.Sort sort : sorts) {
            var propertyName = CamelCaseHelper.toCamelFromUnderscore(sort.name());
            var expressionPath = beanTypeLv.get().expressionPath(propertyName);
            if (expressionPath == null) {
                log.debug("不存在的排序属性 {} {}", beanTypeLv.get(), propertyName);
                continue;
            }
            query.orderBy().add(new OrderBy.Property(propertyName, sort.direction() == DataFilter.Direction.asc));
        }

        var filters = filter.filters();
        for (DataFilter.Filter filterNode : filters) {
            if (filterNode.op() == DataFilter.Op.none) {
                continue;
            }
            var rawValue = filterNode.rawValue();
            var propertyName = CamelCaseHelper.toCamelFromUnderscore(filterNode.name());
            var path = beanTypeLv.get().expressionPath(propertyName);
            if (path == null) {
                log.debug("不存在的过滤属性 {} {}", beanTypeLv.get(), propertyName);
                continue;
            }
            switch (filterNode.op()) {
                case eq:
                    query.where().eq(path.elName(), path.stringParser().parse(rawValue));
                    break;
                case ne:
                    query.where().ne(path.elName(), path.stringParser().parse(rawValue));
                    break;
                case gt:
                    query.where().gt(path.elName(), path.stringParser().parse(rawValue));
                    break;
                case gte:
                    query.where().ge(path.elName(), path.stringParser().parse(rawValue));
                    break;
                case lt:
                    query.where().lt(path.elName(), path.stringParser().parse(rawValue));
                    break;
                case lte:
                    query.where().le(path.elName(), path.stringParser().parse(rawValue));
                    break;
                case contains:
                    query.where().contains(path.elName(), rawValue);
                    break;
                case icontains:
                    query.where().icontains(path.elName(), rawValue);
                    break;
                case in: {
                    var list = Arrays.stream(filterNode.values())
                            .map(path.stringParser()::parse)
                            .toList();
                    if (list.isEmpty()) {
                        break;
                    }
                    query.where().in(path.elName(), list);
                    break;
                }
                case nin: {
                    var list = Arrays.stream(filterNode.values())
                            .map(path.stringParser()::parse)
                            .toList();
                    if (list.isEmpty()) {
                        break;
                    }
                    query.where().notIn(path.elName(), list);
                    break;
                }
                case between: {
                    var list = Arrays.stream(filterNode.values())
                            .map(path.stringParser()::parse)
                            .toList();
                    if (list.isEmpty() || list.size() < 2) {
                        break;
                    }
                    query.where().between(path.elName(), list.get(0), list.get(1));
                    break;
                }
            }
        }

        if (filter.offset() > 0) {
            query.setFirstRow(filter.offset());
        }

        if (filter.limit() > 0) {
            query.setMaxRows(filter.limit());
        }

        // 包含软删除的数据
        if (filter.isIncludeDeleted()) {
            query.setIncludeSoftDeletes();
        }
    }


    /// 将过滤条件应用到查询上
    ///
    /// @param filter 数据过滤条件
    /// @param ctx RSQL 上下文
    protected void apply(DataFilter filter, RSQLContext ctx) {
        var node = RSQL_PARSER.parse(filter.filter());
        node.accept(new EBeanRSQLVisitor(), ctx);

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

        var sorts = filter.sorts();
        for (DataFilter.Sort sort : sorts) {
            var property = ctx.getProperty(sort.name());
            if (property == null) {
                log.debug("不存在的排序属性 {}", sort.name());
                continue;
            }

            if (sort.direction() == DataFilter.Direction.asc) {
                property.original().asc();
            } else {
                property.original().desc();
            }
        }
    }

    /// 分页过滤数据
    ///
    /// @param query  查询对象
    /// @param filter 数据过滤条件
    protected PagedList<T> findPagedList(Query<T> query, DataFilter filter) {
        apply(query, filter);
        var plist = query.findPagedList();
        if (filter.isNoTotalCount()) {
            return new NoTotalCountPagedList<>(plist);
        }
        return plist;
    }

    protected static class NoTotalCountPagedList<T> implements PagedList<T> {

        private final PagedList<T> plist;

        private NoTotalCountPagedList(PagedList<T> plist) {
            this.plist = plist;
        }

        @Override
        public void loadCount() {
            // do nothing
        }

        @Override
        public Future<Integer> getFutureCount() {
            return CompletableFuture.completedFuture(-1);
        }

        @Override
        public List<T> getList() {
            return plist.getList();
        }

        @Override
        public int getTotalCount() {
            return -1;
        }

        @Override
        public int getTotalPageCount() {
            return -1;
        }

        @Override
        public int getPageSize() {
            return -1;
        }

        @Override
        public int getPageIndex() {
            return -1;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasPrev() {
            return false;
        }

        @Override
        public String getDisplayXtoYofZ(String to, String of) {
            return plist.getDisplayXtoYofZ(to, of);
        }
    }
}
