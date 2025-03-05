package cool.houge.mahu.entity.mart;

import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

/// 产品属性
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "product_attribute", schema = "mart")
public class ProductAttribute {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;
    /// 创建时间
    @WhenCreated
    private LocalDateTime createdAt;
    /// 更新时间
    @WhenModified
    private LocalDateTime updatedAt;
    /// 软删除
    @SoftDelete
    private boolean deleted;
    /// 产品
    @ManyToOne
    private Product product;
    /// 属性
    @ManyToOne
    private Attribute attribute;
    /// 属性值
    private String value;
}
