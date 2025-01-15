package cool.houge.mahu.entity.market;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "product_variant_attribute", schema = "market")
public class ProductVariantAttribute {

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
    /// 产品属性
    @ManyToOne
    private ProductAttribute productAttribute;
    /// 产品属性值
    @ManyToOne
    private AttributeValue attributeValue;
    /// 属性值（适用手动输入的属性）
    private String value;
}
