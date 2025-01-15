package cool.houge.mahu.entity.market;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 产品属性
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "product_attribute", schema = "market")
public class ProductAttribute {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 更新时间
    @WhenModified
    private Instant updateTime;
    /// 产品
    @ManyToOne
    private Product product;
    /// 属性
    @ManyToOne
    private Attribute attribute;
    /// 属性值
    @ManyToOne
    private AttributeValue attributeValue;
    /// 属性值（适用手动输入的属性）
    private String value;
    /// 变体属性（规格）
    private boolean variant;
}
