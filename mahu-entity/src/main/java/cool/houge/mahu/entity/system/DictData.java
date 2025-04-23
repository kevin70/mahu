package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 数据字典
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "dict_data", schema = "system")
public class DictData implements Auditable {

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
    /// 数据字典代码，唯一
    private String dataCode;
    /// 数据字典类型
    @ManyToOne
    @JoinColumn(name = "type_code")
    private DictType dictType;
    /// 数据字典名称
    private String name;
    /// 数据字典值
    private String value;
    /// 排序值
    @OrderBy("DESC")
    private Integer ordering;
    /// 是否禁用
    ///
    /// - `true`：禁用
    /// - `false`：启用
    private boolean disabled;
}
