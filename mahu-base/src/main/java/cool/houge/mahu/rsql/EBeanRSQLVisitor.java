package cool.houge.mahu.rsql;

import static cz.jirutka.rsql.parser.ast.RSQLOperators.EQUAL;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.GREATER_THAN;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.GREATER_THAN_OR_EQUAL;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.IN;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.IS_NULL;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.LESS_THAN;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.LESS_THAN_OR_EQUAL;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.NOT_EQUAL;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.NOT_IN;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.NOT_NULL;

import com.google.common.base.Strings;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import io.ebean.Expr;
import java.util.List;
import java.util.Objects;

/// EBean RSQL 表达式解析器。
///
/// 将 RSQL 语法转换为 EBean 查询条件支持标准 RSQL 操作符及扩展操作符（LIKE、ILIKE 等）
///
/// @author ZY (kzou227@qq.com)
public class EBeanRSQLVisitor implements RSQLVisitor<Void, RSQLContext> {

    @Override
    public Void visit(AndNode node, RSQLContext ctx) {
        if (node.getChildren().isEmpty()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "AND 条件不能为空");
        }

        ctx.query().and();
        try {
            for (Node child : node.getChildren()) {
                child.accept(this, ctx);
            }
        } finally {
            // 确保分组正确关闭
            ctx.query().endAnd();
        }
        return null;
    }

    @Override
    public Void visit(OrNode node, RSQLContext ctx) {
        if (node.getChildren().isEmpty()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "OR 条件不能为空");
        }

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

        // 转换参数值并处理转换异常
        List<?> args;
        try {
            args = node.getArguments().stream()
                    .map(arg -> convertValue(item, arg)) // 单独抽取转换逻辑
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new BizCodeException(
                    BizCodes.INVALID_ARGUMENT,
                    Strings.lenientFormat("属性[%s]的值转换失败: %s", node.getSelector(), e.getMessage()),
                    e);
        }

        this.addComparisonExpression(ctx, item, node.getOperator(), args);
        return null;
    }

    /// 转换参数值，增加空值处理
    private Object convertValue(FilterItem item, String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }
        return item.getValueConverter().apply(rawValue);
    }

    /// 根据操作符添加对应的查询条件
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    private void addComparisonExpression(RSQLContext ctx, FilterItem item, ComparisonOperator op, List<?> args) {
        String column = item.getColumn();
        var qb = ctx.query();
        validateArguments(op, args);

        // 根据操作符添加对应条件（使用 switch 表达式优化可读性）
        if (op.equals(EQUAL)) {
            qb.add(Expr.eq(column, args.get(0)));
        } else if (op.equals(NOT_EQUAL)) {
            qb.add(Expr.ne(column, args.get(0)));
        } else if (op.equals(GREATER_THAN)) {
            qb.add(Expr.gt(column, args.get(0)));
        } else if (op.equals(LESS_THAN)) {
            qb.add(Expr.lt(column, args.get(0)));
        } else if (op.equals(GREATER_THAN_OR_EQUAL)) {
            qb.add(Expr.ge(column, args.get(0)));
        } else if (op.equals(LESS_THAN_OR_EQUAL)) {
            qb.add(Expr.le(column, args.get(0)));
        } else if (op.equals(IN)) {
            qb.add(Expr.inOrEmpty(column, args));
        } else if (op.equals(NOT_IN)) {
            qb.add(Expr.not(Expr.inOrEmpty(column, args)));
        } else if (op.equals(IS_NULL)) {
            qb.add(Expr.isNull(column));
        } else if (op.equals(NOT_NULL)) {
            qb.add(Expr.isNotNull(column));
        } else if (op.equals(ExtRSQLOperators.LIKE)) {
            qb.add(Expr.like(column, args.get(0).toString()));
        } else if (op.equals(ExtRSQLOperators.ILIKE)) {
            qb.add(Expr.ilike(column, args.get(0).toString()));
        } else if (op.equals(ExtRSQLOperators.CONTAINS)) {
            qb.add(Expr.contains(column, args.get(0).toString()));
        } else if (op.equals(ExtRSQLOperators.ICONTAINS)) {
            qb.add(Expr.icontains(column, args.get(0).toString()));
        } else if (op.equals(ExtRSQLOperators.BETWEEN)) {
            qb.add(Expr.between(column, args.get(0), args.get(1)));
        } else {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("不支持的操作符: %s", op));
        }
    }

    /// 参数校验：确保操作符与参数数量匹配
    private void validateArguments(ComparisonOperator op, List<?> args) {
        // 不需要参数的操作符
        if (op.getArity().max() == 0 && !args.isEmpty()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("操作符[%s]不需要参数", op));
        }

        // 需要单个参数的操作符
        if (op.getArity().max() == 1 && args.size() != 1) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("操作符[%s]需要且仅需要一个参数", op));
        }

        // 需要两个参数的操作符
        if (op.getArity().max() == 2 && args.size() != 2) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("操作符[%s]需要且仅需要两个参数", op));
        }

        // 需要至少一个参数
        if (op.getArity().max() >= 1 && args.isEmpty()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("操作符[%s]需要至少一个参数", op));
        }
    }
}
