package cool.houge.mahu.entity.system;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/// 数据字典类型
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "dict_type", schema = "system")
public class DictType {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 创建时间
    @WhenCreated
    private LocalDateTime createdAt;
    /// 更新时间
    @WhenModified
    private LocalDateTime updatedAt;
    /// 字典类型编码，唯一
    private String typeCode;
    /// 字典类型名称
    private String name;
    /// 字典类型描述
    private String description;
    /// 状态
    ///
    /// - `true`：启用
    /// - `false`：禁用
    private boolean status;
}
