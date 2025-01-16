package cool.houge.mahu.entity.mart;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.SoftDelete;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/// 商品属性可选值
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "attribute_value", schema = "mart")
public class AttributeValue implements Auditable {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 软删除
    @SoftDelete
    private Boolean deleted;
    /// 属性
    @ManyToOne
    private Attribute attribute;
    /// 可选值
    private String value;
    /// 排序值
    private Integer ordering;
}
