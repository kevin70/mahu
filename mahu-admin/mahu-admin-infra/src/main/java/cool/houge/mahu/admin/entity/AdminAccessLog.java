package cool.houge.mahu.admin.entity;

import cool.houge.mahu.entity.log.BaseBizLog;
import io.ebean.annotation.WhoCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/// 管理员后台访问记录
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "log", name = "admin_access_log")
public class AdminAccessLog extends BaseBizLog {

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
