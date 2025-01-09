package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/// 组织架构
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "department", schema = "system")
public class Department implements Auditable {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 更新时间
    @WhenModified
    private Instant updateTime;
    /// 名称
    private String name;
    /// 父节点
    @ManyToOne
    private Department parent;
    /// 负责人
    @ManyToOne
    private Employee leader;
    /// 排序值
    private Integer ordering;

    /// 组织深度
    @Transient
    private Integer depth;
    /// 子元素
    @Transient
    private List<Department> children;
}
