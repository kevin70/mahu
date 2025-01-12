package cool.houge.mahu.entity.market;

import cool.houge.mahu.entity.Auditable;
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
@Table(name = "attribute", schema = "market")
public class Attribute implements Auditable {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 属性的值的来源类型
    @Enumerated
    private ValueType valueType;
    /// 名称
    private String name;
    /// 描述
    private String remark;
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
