package cool.houge.mahu.common.rsql;

import com.google.common.base.Strings;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cz.jirutka.rsql.parser.ast.*;
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
        var where = ctx.query().where();
        where.or();
        for (Node n : node.getChildren()) {
            n.accept(this, ctx);
        }
        where.endOr();
        return null;
    }

    @Override
    public Void visit(ComparisonNode node, RSQLContext ctx) {
        var filterField = ctx.getFilterField(node.getSelector());
        if (filterField == null) {
            return null;
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
            return null;
        }

        var columnName = filterField.getColumnName();
        var where = ctx.query().where();
        if (op.equals(RSQLOperators.EQUAL)) {
            where.add(Expr.eq(columnName, args.getFirst()));
        } else if (op.equals(RSQLOperators.NOT_EQUAL)) {
            where.add(Expr.ne(columnName, args.getFirst()));
        } else if (op.equals(RSQLOperators.GREATER_THAN)) {
            where.add(Expr.gt(columnName, args.getFirst()));
        } else if (op.equals(RSQLOperators.LESS_THAN)) {
            where.add(Expr.lt(columnName, args.getFirst()));
        } else if (op.equals(RSQLOperators.GREATER_THAN_OR_EQUAL)) {
            where.add(Expr.ge(columnName, args.getFirst()));
        } else if (op.equals(RSQLOperators.LESS_THAN_OR_EQUAL)) {
            where.add(Expr.le(columnName, args.getFirst()));
        } else if (op.equals(RSQLOperators.IN)) {
            where.add(Expr.in(columnName, args));
        } else if (op.equals(RSQLOperators.NOT_IN)) {
            where.add(Expr.not(Expr.in(columnName, args)));
        } else if (op.equals(RSQLOperators.IS_NULL)) {
            where.add(Expr.isNull(columnName));
        } else if (op.equals(RSQLOperators.NOT_NULL)) {
            where.add(Expr.isNotNull(columnName));
        } else if (op.equals(RSQLOperators.LIKE)) {
            where.add(Expr.like(columnName, args.getFirst().toString()));
        } else if (op.equals(RSQLOperators.ILIKE)) {
            where.add(Expr.ilike(columnName, args.getFirst().toString()));
        } else if (op.equals(RSQLOperators.CONTAINS)) {
            where.add(Expr.contains(columnName, args.getFirst().toString()));
        } else if (op.equals(RSQLOperators.ICONTAINS)) {
            where.add(Expr.icontains(columnName, args.getFirst().toString()));
        } else if (op.equals(RSQLOperators.BETWEEN)) {
            where.add(Expr.between(columnName, args.getFirst(), args.getLast()));
        }
        return null;
    }
}
