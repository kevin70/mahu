package cool.houge.mahu.entity.system;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/// 后台访问记录
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "system", name = "access_log")
public class AccessLog {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;
    /// 创建时间
    @WhenCreated
    private LocalDateTime createdAt;
    /// 访问职员 ID
    private Long employeeId;
    /// 访问 IP
    private String ipAddr;
    /// 追踪 ID
    private String traceId;
    /// 客户端/服务器连接套接字的标识（不保证唯一）
    private String channelId;
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
    /// 响应状态码
    private Integer responseStatus;
    /// 响应字节数
    private Long responseBytes;
    /// UA
    private String userAgent;
}
