package cool.houge.mahu.entity.mart;

import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 产品变体属性
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
    private Product product;
    /// 产品变体
    @ManyToOne
    private ProductVariant productVariant;
    /// 产品属性
    @ManyToOne
    private ProductAttribute attribute;
    /// 属性值
    private String value;
}
