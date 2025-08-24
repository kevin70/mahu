package cool.houge.mahu.rsql;

import cz.jirutka.rsql.parser.ast.Arity;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;

/// RSQL 操作符
///
/// @author ZY (kzou227@qq.com)
@UtilityClass
public final class ExtRSQLOperators {

    public static final ComparisonOperator LIKE = new ComparisonOperator("=ke=", "=like=", Arity.nary(1));
    public static final ComparisonOperator ILIKE = new ComparisonOperator("=ik=", "=ilike=", Arity.nary(1));
    public static final ComparisonOperator CONTAINS = new ComparisonOperator("=cs=", "=contains=", Arity.nary(1));
    public static final ComparisonOperator ICONTAINS = new ComparisonOperator("=ics=", "=icontains=", Arity.nary(1));
    public static final ComparisonOperator BETWEEN = new ComparisonOperator("=bt=", "=between=", Arity.nary(2));

    private static final Set<ComparisonOperator> OPERATORS;

    static {
        var set = new HashSet<>(RSQLOperators.defaultOperators());
        set.add(LIKE);
        set.add(ILIKE);
        set.add(CONTAINS);
        set.add(ICONTAINS);
        set.add(BETWEEN);
        OPERATORS = Set.copyOf(set);
    }

    /// 返回 RSQL 实现的操作符
    public static Set<ComparisonOperator> supportedOperators() {
        return OPERATORS;
    }
}
