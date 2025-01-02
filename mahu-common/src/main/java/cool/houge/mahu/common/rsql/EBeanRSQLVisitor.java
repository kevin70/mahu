package cool.houge.mahu.common.rsql;

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
        var qb = ctx.queryBean();
        qb.or();
        for (Node n : node.getChildren()) {
            n.accept(this, ctx);
        }
        qb.endOr();
        return null;
    }

    @Override
    public Void visit(ComparisonNode node, RSQLContext ctx) {
        var op = node.getOperator();
        var property = ctx.getProperty(node.getSelector());
        if (property == null) {
            return null;
        }

        List<?> args;
        try {
            args = node.getArguments().stream().map(property.converter()).toList();
        } catch (IllegalArgumentException e) {
            return null;
        }

        var qb = ctx.queryBean();
        if (op.equals(RSQLOperators.EQUAL)) {
            qb.add(Expr.eq(property.propertyName(), args.getFirst()));
        } else if (op.equals(RSQLOperators.NOT_EQUAL)) {
            qb.add(Expr.ne(property.propertyName(), args.getFirst()));
        } else if(op.equals(RSQLOperators.GREATER_THAN)) {
            qb.add(Expr.gt(property.propertyName(), args.getFirst()));
        } else if(op.equals(RSQLOperators.LESS_THAN)) {
            qb.add(Expr.lt(property.propertyName(), args.getFirst()));
        } else if(op.equals(RSQLOperators.GREATER_THAN_OR_EQUAL)){
            qb.add(Expr.ge(property.propertyName(), args.getFirst()));
        } else if(op.equals(RSQLOperators.LESS_THAN_OR_EQUAL)){
            qb.add(Expr.le(property.propertyName(), args.getFirst()));
        } else if(op.equals(RSQLOperators.IN)) {
            qb.add(Expr.in(property.propertyName(), args));
        } else if(op.equals(RSQLOperators.NOT_IN)) {
            qb.add(Expr.not(Expr.in(property.propertyName(), args)));
        } else if(op.equals(RSQLOperators.IS_NULL)){
            qb.add(Expr.isNull(property.propertyName()));
        } else if (op.equals(RSQLOperators.NOT_NULL)) {
            qb.add(Expr.isNotNull(property.propertyName()));
        } else if(op.equals(RSQLOperators.LIKE)) {
            qb.add(Expr.like(property.propertyName(), args.getFirst().toString()));
        }  else if (op.equals(RSQLOperators.ILIKE)) {
            qb.add(Expr.ilike(property.propertyName(), args.getFirst().toString()));
        } else if(op.equals(RSQLOperators.BETWEEN)){
            qb.add(Expr.between(property.propertyName(), args.getFirst(), args.getLast()));
        }
        return null;
    }
}
