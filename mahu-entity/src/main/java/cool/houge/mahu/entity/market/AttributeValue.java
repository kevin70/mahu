package cool.houge.mahu.entity.market;

import cool.houge.mahu.entity.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/// 商品属性可选值
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "attribute_value", schema = "market")
public class AttributeValue implements Auditable {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 可选值
    private String value;
    /// 排序值
    private Integer ordering;
    /// 属性
    @ManyToOne
    private Attribute attribute;
}
