package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private String typeCode;
    /// 创建时间
    @WhenCreated
    private LocalDateTime createdAt;
    /// 更新时间
    @WhenModified
    private LocalDateTime updatedAt;
    /// 字典类型名称
    private String name;
    /// 字典类型描述
    private String description;
    /// 状态
    ///
    /// - `true`：启用
    /// - `false`：禁用
    private boolean status;
    /// 字典数据
    @OneToMany(mappedBy = "dictType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DictData> data;
}
