package cool.houge.mahu.entity;

import io.ebean.annotation.ChangeLog;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/// 数据字典分组
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@ChangeLog(updatesThatInclude = {"name", "description", "enabled", "visibility", "valueRegex", "preset"})
@Entity
@Table(name = "dict_groups")
public class DictGroup {

    /// 字典分组编码，唯一
    @Id
    private String id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 字典分组名称
    private String name;
    /// 描述
    private String description;
    /// 是否启动
    ///
    /// - `true`：启用
    /// - `false`：禁用
    private Boolean enabled;
    /// 可见性配置
    @Enumerated
    private Visibility visibility;
    /// 值正则表达式规则
    private String valueRegex;
    /// 是否预置
    private boolean preset;
    /// 字典数据
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dict> data;

    /// 可见性
    public enum Visibility {
        /// 私有的，仅限内部使用
        PRIVATE,
        /// 公共的
        PUBLIC,
        /// 受限的
        RESTRICTED
    }
}
