package cool.houge.mahu.entity;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/// 数据字典
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "dict")
public class Dict implements Auditable {

    /// 字典代码
    @Id
    private Integer dc;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 数据字典类型
    @ManyToOne
    private DictType type;
    /// 标签
    private String label;
    /// 值
    private String value;
    /// 是否禁用
    ///
    /// - `true`：启用
    /// - `false`：禁用
    private Boolean enabled;
    /// 排序值
    @OrderBy("DESC")
    private Integer ordering;
}
