package cool.houge.mahu.entity;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/// 数据字典类型
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "dict_type", schema = "system")
public class DictType implements Auditable {

    /// 字典类型编码，唯一
    @Id
    private String type;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 字典类型名称
    private String name;
    /// 是否禁用
    ///
    /// - `true`：禁用
    /// - `false`：启用
    private Boolean disabled;
    /// 字典数据
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dict> data;
}
