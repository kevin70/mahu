package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "dict", schema = "system")
public class Dict implements Auditable {

    /// 字典代码
    @Id
    private String code;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 数据字典类型
    @ManyToOne
    @JoinColumn(name = "type_code")
    private DictType dictType;
    /// 名称
    private String name;
    /// 值
    private String value;
    /// 排序值
    @OrderBy("DESC")
    private int ordering;
    /// 是否禁用
    ///
    /// - `true`：禁用
    /// - `false`：启用
    private boolean disabled;
}
