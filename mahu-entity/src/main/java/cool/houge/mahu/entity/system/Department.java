package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 名称
    private String name;
    /// 父节点
    @ManyToOne
    private Department parent;
    /// 负责人
    @ManyToOne
    private Admin leader;
    /// 排序值
    private Integer ordering;

    /// 组织深度
    @Transient
    private Integer depth;
    /// 子元素
    @Transient
    private List<Department> children;
}
