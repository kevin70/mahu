package cool.houge.mahu.rsql;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cz.jirutka.rsql.parser.ast.*;
import io.ebean.Expr;
import io.ebean.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import static cool.houge.mahu.rsql.ExtRSQLOperators.*;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.*;

/// EBean RSQL 表达式解析器。
///
/// 将 RSQL 语法转换为 EBean 查询条件支持标准 RSQL 操作符及扩展操作符（LIKE、ILIKE 等）
///
/// @author ZY (kzou227@qq.com)
public class EBeanRSQLVisitor implements RSQLVisitor<Void, RSQLContext> {

    private static final Logger log = LoggerFactory.getLogger(EBeanRSQLVisitor.class);

    // 映射 ComparisonOperator 到创建 Expression 的 BiFunction
    // 这个映射用于根据操作符生成相应的 EBean 表达式
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    private static final Map<ComparisonOperator, BiFunction<String, List<?>, Expression>> OPERATOR_EXPRESSION_MAP =
            ImmutableMap.<ComparisonOperator, BiFunction<String, List<?>, Expression>>builder()
                    .put(EQUAL, (col, a) -> Expr.eq(col, a.get(0)))
                    .put(NOT_EQUAL, (col, a) -> Expr.ne(col, a.get(0)))
                    .put(GREATER_THAN, (col, a) -> Expr.gt(col, a.get(0)))
                    .put(LESS_THAN, (col, a) -> Expr.lt(col, a.get(0)))
                    .put(GREATER_THAN_OR_EQUAL, (col, a) -> Expr.ge(col, a.get(0)))
                    .put(LESS_THAN_OR_EQUAL, (col, a) -> Expr.le(col, a.get(0)))
                    .put(IN, Expr::inOrEmpty)
                    .put(NOT_IN, (col, a) -> Expr.not(Expr.inOrEmpty(col, a)))
                    .put(IS_NULL, (col, a) -> Expr.isNull(col))
                    .put(NOT_NULL, (col, a) -> Expr.isNotNull(col))
                    .put(LIKE, (col, a) -> Expr.like(col, a.get(0).toString()))
                    .put(ILIKE, (col, a) -> Expr.ilike(col, a.get(0).toString()))
                    .put(CONTAINS, (col, a) -> Expr.contains(col, a.get(0).toString()))
                    .put(ICONTAINS, (col, a) -> Expr.icontains(col, a.get(0).toString()))
                    .put(BETWEEN, (col, a) -> Expr.between(col, a.get(0), a.get(1)))
                    .build();

    @Override
    public Void visit(AndNode node, RSQLContext ctx) {
        if (node.getChildren().isEmpty()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "AND 条件不能为空");
        }

        log.debug("Visiting AND node with children: {}", node.getChildren());
        ctx.query().and();
        try {
            for (Node child : node.getChildren()) {
                child.accept(this, ctx);
            }
        } finally {
            ctx.query().endAnd();
        }
        return null;
    }

    @Override
    public Void visit(OrNode node, RSQLContext ctx) {
        if (node.getChildren().isEmpty()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "OR 条件不能为空");
        }

        log.debug("Visiting OR node with children: {}", node.getChildren());
        ctx.query().or();
        try {
            for (Node child : node.getChildren()) {
                child.accept(this, ctx);
            }
        } finally {
            ctx.query().endOr();
        }
        return null;
    }

    @Override
    public Void visit(ComparisonNode node, RSQLContext ctx) {
        var item = ctx.getFilterField(node.getSelector());
        if (item == null) {
            throw new BizCodeException(
                    BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("不支持的过滤属性: [%s]", node.getSelector()));
        }

        var args = convertArguments(node, item);
        var op = node.getOperator();
        validateArguments(op, args);
        log.debug("Creating expression for column: {}, operator: {}, arguments: {}", item.getColumn(), op, args);
        ctx.query().add(createExpression(item.getColumn(), op, args));
        return null;
    }

    private Expression createExpression(String column, ComparisonOperator op, List<?> args) {
        var f = OPERATOR_EXPRESSION_MAP.get(op);
        if (f == null) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("不支持的操作符: %s", op));
        }
        return f.apply(column, args);
    }

    /// 转换参数值列表，处理空值和转换异常
    private List<?> convertArguments(ComparisonNode node, FilterItem item) {
        try {
            return node.getArguments().stream()
                    .map(arg -> convertValue(item, arg)) // 单独抽取转换逻辑
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new BizCodeException(
                    BizCodes.INVALID_ARGUMENT,
                    Strings.lenientFormat("属性[%s]的值转换失败: %s", node.getSelector(), e.getMessage()),
                    e);
        }
    }

    /// 转换参数值，增加空值处理
    private Object convertValue(FilterItem item, String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }
        return item.getValueConverter().apply(rawValue);
    }

    /// 参数校验：确保操作符与参数数量匹配
    private void validateArguments(ComparisonOperator op, List<?> args) {
        int maxArity = op.getArity().max();
        if (maxArity == 0 && !args.isEmpty()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("操作符[%s]不需要参数", op));
        } else if (maxArity == 1 && args.size() != 1) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("操作符[%s]需要且仅需要一个参数", op));
        } else if (maxArity == 2 && args.size() != 2) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("操作符[%s]需要且仅需要两个参数", op));
        } else if (maxArity >= 1 && args.isEmpty()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("操作符[%s]需要至少一个参数", op));
        }
    }
}
