package cool.houge.mahu.entity.mart;

import io.ebean.annotation.SoftDelete;
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
@Table(name = "product_variant_attribute", schema = "mart")
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
    /// 软删除
    @SoftDelete
    private boolean deleted;
    /// 产品
    @ManyToOne
    private ProductVariant productVariant;
    /// 产品属性
    @ManyToOne
    private ProductAttribute productAttribute;
    /// 产品属性值
    @ManyToOne
    private AttributeValue attributeValue;
    /// 属性值（适用手动输入的属性）
    private String value;
}
