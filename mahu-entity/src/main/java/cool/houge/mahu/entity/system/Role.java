package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/// 角色.
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "role", schema = "system")
@ChangeLog
public class Role implements Auditable {

    /// 主键
    @Id
    private Integer id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 更新时间
    @WhenModified
    private Instant updateTime;
    /// 软删除
    @SoftDelete
    private Boolean deleted;
    /// 角色名称
    private String name;
    /// 备注
    private String remark;
    /// 排序值
    private Integer ordering;
    /// 权限代码
    @DbArray
    private List<String> permits;
}
