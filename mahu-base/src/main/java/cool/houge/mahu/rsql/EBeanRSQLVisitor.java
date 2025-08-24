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

/// EBean RSQL 实现
///
/// @author ZY (kzou227@qq.com)
public class EBeanRSQLVisitor implements RSQLVisitor<Void, RSQLContext> {

    @Override
    public Void visit(AndNode node, RSQLContext ctx) {
        for (Node n : node.getChildren()) {
            n.accept(this, ctx);
        }
        return null;
    }

    @Override
    public Void visit(OrNode node, RSQLContext ctx) {
        var qb = ctx.query();
        qb.or();
        for (Node n : node.getChildren()) {
            n.accept(this, ctx);
        }
        qb.endOr();
        return null;
    }

    @Override
    public Void visit(ComparisonNode node, RSQLContext ctx) {
        var item = ctx.getFilterField(node.getSelector());
        if (item == null) {
            throw new BizCodeException(
                    BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("不支持属性[%s]", node.getSelector()));
        }

        var op = node.getOperator();
        List<?> args;
        try {
            args = node.getArguments().stream()
                    .map(item.getValueConverter())
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new BizCodeException(
                    BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("非法的数据过滤值", node.getSelector()), e);
        }

        this.addExpr(ctx, item, op, args);
        return null;
    }

    private void addExpr(RSQLContext ctx, FilterItem item, ComparisonOperator op, List<?> args) {
        var column = item.getColumn();
        var qb = ctx.query();
        if (op.equals(EQUAL)) {
            qb.add(Expr.eq(column, args.getFirst()));
        } else if (op.equals(NOT_EQUAL)) {
            qb.add(Expr.ne(column, args.getFirst()));
        } else if (op.equals(GREATER_THAN)) {
            qb.add(Expr.gt(column, args.getFirst()));
        } else if (op.equals(LESS_THAN)) {
            qb.add(Expr.lt(column, args.getFirst()));
        } else if (op.equals(GREATER_THAN_OR_EQUAL)) {
            qb.add(Expr.ge(column, args.getFirst()));
        } else if (op.equals(LESS_THAN_OR_EQUAL)) {
            qb.add(Expr.le(column, args.getFirst()));
        } else if (op.equals(IN)) {
            qb.add(Expr.inOrEmpty(column, args));
        } else if (op.equals(NOT_IN)) {
            qb.add(Expr.not(Expr.inOrEmpty(column, args)));
        } else if (op.equals(IS_NULL)) {
            qb.add(Expr.isNull(column));
        } else if (op.equals(NOT_NULL)) {
            qb.add(Expr.isNotNull(column));
        } else if (op.equals(ExtRSQLOperators.LIKE)) {
            qb.add(Expr.like(column, args.getFirst().toString()));
        } else if (op.equals(ExtRSQLOperators.ILIKE)) {
            qb.add(Expr.ilike(column, args.getFirst().toString()));
        } else if (op.equals(ExtRSQLOperators.CONTAINS)) {
            qb.add(Expr.contains(column, args.getFirst().toString()));
        } else if (op.equals(ExtRSQLOperators.ICONTAINS)) {
            qb.add(Expr.icontains(column, args.getFirst().toString()));
        } else if (op.equals(ExtRSQLOperators.BETWEEN)) {
            qb.add(Expr.between(column, args.getFirst(), args.getLast()));
        }
    }
}
