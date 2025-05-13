package cool.houge.mahu.common.rsql;

import static org.assertj.core.api.Assertions.assertThat;

import cool.houge.mahu.common.rsql.query.QBean;
import cz.jirutka.rsql.parser.RSQLParser;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import java.util.List;
import org.junit.jupiter.api.Test;

class EBeanRSQLVisitorTest {

    @Test
    void parse() {
        var filter = "genres=in=(sci-fi,action) and (director=='Christopher Nolan' or actor==*Bale) and year>=2000";
        var parser = new RSQLParser(MyRSQLOperators.supportedOperators());

        var qb = new QBean(db());
        var node = parser.parse(filter);
        var ctx = new RSQLContext(
                List.of(
                        FilterField.builder().with(qb.genres).build(),
                        FilterField.builder().with(qb.actor).build(),
                        FilterField.builder().with(qb.director).build(),
                        FilterField.builder().with(qb.year).build()),
                qb);
        node.accept(new EBeanRSQLVisitor(), ctx);

        try {
            qb.findList();
        } catch (Exception _) {
            // ignore
        }

        var sql =
                "select /* EBeanRSQLVisitorTest.parse */ t0.genres, t0.director, t0.actor, t0.year from bean t0 where t0.genres in (?,?) and (t0.director = ? or t0.actor = ?) and t0.year >= ?";
        System.out.println(qb.getGeneratedSql());
        assertThat(qb.getGeneratedSql()).isEqualTo(sql);
    }

    private Database db() {
        var pool = new DataSourceConfig()
                .setName("test")
                .setUrl("jdbc:h2:mem:tests")
                .setUsername("sa")
                .setPassword("")
                .build();
        var dbc = new DatabaseConfig();
        dbc.setDataSource(pool);
        return DatabaseFactory.create(dbc);
    }
}
