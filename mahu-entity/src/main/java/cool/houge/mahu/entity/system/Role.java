package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.DbJson;
import io.ebean.annotation.DbJsonType;
import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/// 系统角色
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "role", schema = "system")
public class Role implements Auditable {

    /// 主键
    @Id
    private Integer id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
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
    @DbJson(storage = DbJsonType.JSONB)
    private List<String> permissions;
}
