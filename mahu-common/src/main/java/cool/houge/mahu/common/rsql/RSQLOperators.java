package cool.houge.mahu.common.rsql;

import cz.jirutka.rsql.parser.ast.Arity;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;

import java.util.HashSet;
import java.util.Set;

/// RSQL 操作符
///
/// @author ZY (kzou227@qq.com)
public abstract class RSQLOperators extends cz.jirutka.rsql.parser.ast.RSQLOperators {

    public static final ComparisonOperator LIKE = new ComparisonOperator("=ke=", "=like=", Arity.nary(1)),
            ILIKE = new ComparisonOperator("=ik=", "=ilike=", Arity.nary(1)),
            BETWEEN = new ComparisonOperator("=bt=", "=between=", Arity.nary(2));

    private static final Set<ComparisonOperator> OPERATORS;

    static {
        var set = new HashSet<>(defaultOperators());
        set.add(LIKE);
        set.add(ILIKE);
        set.add(BETWEEN);
        OPERATORS = Set.copyOf(set);
    }

    /// 返回 RSQL 实现的操作符
    public static Set<ComparisonOperator> supportedOperators() {
        return OPERATORS;
    }
}
