package cool.houge.mahu.entity.sys;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhoCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/// 管理员操作审计日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_change_logs")
public class AdminChangeLog {

    /// 日志 ID
    @Id
    protected String id;
    /// 创建时间
    @WhenCreated
    protected Instant createdAt;
    /// 操作管理员 ID
    @WhoCreated
    private Integer adminId;
    /// 操作 IP
    private String ipAddr;
    /// 来源应用
    private String source;
    /// 变更记录项
    @OneToMany(mappedBy = "changeLog")
    private List<AdminChangeItem> items;
}
