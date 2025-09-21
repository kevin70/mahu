package cool.houge.mahu.admin.entity;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhoCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/// 管理员后台访问记录
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_access_log")
public class AdminAccessLog {

    /// 日志追踪 ID。
    ///
    /// Time-Sorted Unique Identifiers [TSID](https://github.com/vladmihalcea/hypersistence-tsid)。
    @Id
    protected Long id;
    /// 创建时间
    @WhenCreated
    protected Instant createdAt;
    /// 操作管理员 ID
    @WhoCreated
    private Long adminId;
    /// 访问 IP
    private String ipAddr;
    /// 请求方法
    private String method;
    /// 请求路径
    private String uriPath;
    /// 请求查询参数
    private String uriQuery;
    /// HTTP referer
    private String referer;
    /// 访问协议
    private String protocol;
    /// HTTP User-Agent
    private String userAgent;
    /// 响应状态码
    private Integer responseStatus;
    /// 响应字节数
    private Long responseBytes;
}
