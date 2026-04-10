package cool.houge.mahu.task;

import cool.houge.mahu.util.MahuDatabaseFactory;
import io.ebean.Database;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 数据库
///
/// @author ZY (kzou227@qq.com)
@Singleton
@RunLevel(RunLevel.STARTUP)
@Weight(Weighted.DEFAULT_WEIGHT + 999)
class DatabaseProvider implements Supplier<Database> {

    final Database v;

    DatabaseProvider(DataSource ds) {
        this.v = MahuDatabaseFactory.create(dbc -> {
            dbc.setDataSourceConfig(MahuDatabaseFactory.dataSourceConfig("mahu-task", ds));
            dbc.setSlowQueryMillis(100);
            dbc.setCurrentUserProvider(() -> {
                throw new UnsupportedOperationException("不支持获取当前用户");
            });
        });
    }

    @Override
    public Database get() {
        return this.v;
    }

    @Service.PreDestroy
    void destroy() {
        this.v.shutdown();
    }
}
