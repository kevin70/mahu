package cool.houge.mahu.common.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import org.junit.jupiter.api.Test;

class EBeanRSQLVisitorTest {

    @Test
    void parse() {
        var filter = "genres=in=(sci-fi,action) and (director=='Christopher Nolan' or actor==*Bale) and year>=2000";
        var parser = new RSQLParser(RSQLOperators.supportedOperators());
        var node = parser.parse(filter);
        node.accept(new EBeanRSQLVisitor<>());
    }
}
