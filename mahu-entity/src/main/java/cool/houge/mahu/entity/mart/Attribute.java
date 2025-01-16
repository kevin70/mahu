package cool.houge.mahu.entity.mart;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.SoftDelete;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/// 商品属性
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "attribute", schema = "mart")
public class Attribute implements Auditable {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 软删除
    @SoftDelete
    private Boolean deleted;
    /// 属性的值的来源类型
    @Enumerated
    private ValueType valueType;
    /// 名称
    private String name;
    /// 描述
    private String remark;
    /// 排序值
    private Integer ordering;
    /// 是否为可搜索的
    private boolean searchable;
    /// 是否为必选项
    private boolean required;
    /// 可选值
    @OneToMany(mappedBy = "attribute", cascade = CascadeType.PERSIST)
    private List<AttributeValue> attributeValues;

    public enum ValueType {
        /// 手动输入
        INPUT,
        /// 选择
        SELECT
    }
}
