package cool.houge.mahu.common.rsql;

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
        var filterField = ctx.getFilterField(node.getSelector());
        if (filterField == null) {
            throw new BizCodeException(
                    BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("不支持属性[%s]", node.getSelector()));
        }

        var op = node.getOperator();
        if (!filterField.isAllowOperator(op)) {
            throw new BizCodeException(
                    BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("属性[%s]不支持[%]操作", node.getSelector(), op));
        }

        List<?> args;
        try {
            args = node.getArguments().stream()
                    .map(filterField.getValueConverter())
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new BizCodeException(
                    BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("非法的数据过滤值", node.getSelector()), e);
        }

        this.addExpr(ctx, filterField, op, args);
        return null;
    }

    private void addExpr(RSQLContext ctx, FilterField filterField, ComparisonOperator op, List<?> args) {
        var columnName = filterField.getColumnName();
        var qb = ctx.query();
        if (op.equals(EQUAL)) {
            qb.add(Expr.eq(columnName, args.getFirst()));
        } else if (op.equals(NOT_EQUAL)) {
            qb.add(Expr.ne(columnName, args.getFirst()));
        } else if (op.equals(GREATER_THAN)) {
            qb.add(Expr.gt(columnName, args.getFirst()));
        } else if (op.equals(LESS_THAN)) {
            qb.add(Expr.lt(columnName, args.getFirst()));
        } else if (op.equals(GREATER_THAN_OR_EQUAL)) {
            qb.add(Expr.ge(columnName, args.getFirst()));
        } else if (op.equals(LESS_THAN_OR_EQUAL)) {
            qb.add(Expr.le(columnName, args.getFirst()));
        } else if (op.equals(IN)) {
            qb.add(Expr.in(columnName, args));
        } else if (op.equals(NOT_IN)) {
            qb.add(Expr.not(Expr.in(columnName, args)));
        } else if (op.equals(IS_NULL)) {
            qb.add(Expr.isNull(columnName));
        } else if (op.equals(NOT_NULL)) {
            qb.add(Expr.isNotNull(columnName));
        } else if (op.equals(MyRSQLOperators.LIKE)) {
            qb.add(Expr.like(columnName, args.getFirst().toString()));
        } else if (op.equals(MyRSQLOperators.ILIKE)) {
            qb.add(Expr.ilike(columnName, args.getFirst().toString()));
        } else if (op.equals(MyRSQLOperators.CONTAINS)) {
            qb.add(Expr.contains(columnName, args.getFirst().toString()));
        } else if (op.equals(MyRSQLOperators.ICONTAINS)) {
            qb.add(Expr.icontains(columnName, args.getFirst().toString()));
        } else if (op.equals(MyRSQLOperators.BETWEEN)) {
            qb.add(Expr.between(columnName, args.getFirst(), args.getLast()));
        }
    }
}
