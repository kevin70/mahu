package cool.houge.mahu.common.rsql;

import cz.jirutka.rsql.parser.ast.*;

/// EBean RSQL 实现
///
/// @author ZY (kzou227@qq.com)
public class EBeanRSQLVisitor<R, A> implements RSQLVisitor<R, A> {
    //

    @Override
    public R visit(AndNode node, A param) {
        for (Node n : node.getChildren()) {
            n.accept(this, param);
        }
        return null;
    }

    @Override
    public R visit(OrNode node, A param) {
        for (Node n : node.getChildren()) {
            n.accept(this, param);
        }
        return null;
    }

    @Override
    public R visit(ComparisonNode node, A param) {
        System.out.println(node);
        return null;
    }
}
