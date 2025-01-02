package cool.houge.mahu.common.rsql;

import cool.houge.mahu.common.rsql.query.QBean;
import cz.jirutka.rsql.parser.RSQLParser;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EBeanRSQLVisitorTest {

    @Test
    void parse() {
        var filter = "genres=in=(sci-fi,action) and (director=='Christopher Nolan' or actor==*Bale) and year>=2000";
        var parser = new RSQLParser(RSQLOperators.supportedOperators());

        var qb = new QBean(db());
        var node = parser.parse(filter);

        qb.orderBy().actor.desc();

        node.accept(
                new EBeanRSQLVisitor(),
                RSQLContext.of(qb)
                        .property(qb.genres)
                        .property(qb.actor)
                        .property(qb.director)
                        .property(qb.year));

        try {
            qb.findList();
        } catch (Exception e) {
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
        var db = DatabaseFactory.create(dbc);
        return db;
    }
}
