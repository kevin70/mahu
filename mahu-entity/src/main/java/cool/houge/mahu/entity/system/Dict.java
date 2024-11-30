package cool.houge.mahu.entity.system;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 字典
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "dict", schema = "system")
public class Dict {

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
    /// 别名（slug）是网址的唯一标识部分，通常位于 URL 的末尾
    private String slug;
    /// 种类
    private String kind;
    /// 值
    private String value;
    /// 文本
    private String label;
    /// 排序值
    private Integer ordering;
    /// 备注
    private String remark;

    /// 预定义的字典种类
    @Getter
    public enum PredefineKinds {
        POSITIONAL_TITLE("职称"),
        ;
        private final String label;

        PredefineKinds(String label) {
            this.label = label;
        }
    }
}
