package cool.houge.mahu.util;

import io.ebean.datasource.DataSourceBuilder;
import io.ebean.datasource.DataSourcePool;

/// Mahu 统一数据源构建工厂。
public final class MahuDataSourceFactory {

    private MahuDataSourceFactory() {}

    public static DataSourceBuilder builder(String name, String url, String username, String password) {
        return DataSourcePool.builder()
                .name(name)
                .url(url)
                .username(username)
                .password(password)
                .cstmtCacheSize(250)
                .pstmtCacheSize(2048)
                .heartbeatSql("SELECT 1")
                .validateOnHeartbeat(true)
                .shutdownOnJvmExit(false);
    }
}
