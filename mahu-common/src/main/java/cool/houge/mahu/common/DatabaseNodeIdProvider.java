package cool.houge.mahu.common;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/// 通过数据库获取 NodeId
///
/// @author ZY (kzou227@qq.com)
public class DatabaseNodeIdProvider implements NodeIdProvider {

    private final String sequenceName;
    private final DataSource dataSource;

    public DatabaseNodeIdProvider(String sequenceName, DataSource dataSource) {
        this.sequenceName = sequenceName;
        this.dataSource = dataSource;
    }

    @Override
    public int getNodeId() {
        var sql = "SELECT nextval('" + sequenceName + "')";
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
