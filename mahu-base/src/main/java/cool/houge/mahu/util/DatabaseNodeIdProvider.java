package cool.houge.mahu.util;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/// 通过数据库获取 NodeId
///
/// @author ZY (kzou227@qq.com)
public class DatabaseNodeIdProvider implements NodeIdProvider {

    private final String sql;
    private final DataSource dataSource;

    public DatabaseNodeIdProvider(String sql, DataSource dataSource) {
        requireNonNull(sql, "sql cannot be null");
        requireNonNull(dataSource, "dataSource cannot be null");

        this.sql = sql;
        this.dataSource = dataSource;
    }

    @Override
    public int getNodeId() {
        try (Connection conn = dataSource.getConnection();
                var statement = conn.createStatement();
                var rs = statement.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("获取 NodeId 失败", e);
        }
        throw new IllegalStateException("未获取到有效的 NodeId");
    }
}
