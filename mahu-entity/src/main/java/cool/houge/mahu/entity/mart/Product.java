package cool.houge.mahu.entity.mart;

import cool.houge.mahu.entity.Brand;
import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/// 产品
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "product", schema = "mart")
public class Product {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 软删除
    @SoftDelete
    private boolean deleted;
    /// 数据版本
    @Version
    private Integer ver;
    /// 产品状态
    @Enumerated(EnumType.ORDINAL)
    private ProductStatus status;
    /// 名称
    private String name;
    /// 描述
    private String description;
    /// 产品图片媒体
    @DbJsonB
    private List<String> images;
    /// 产品类型
    @Enumerated
    private Type type;
    /// 商店
    @ManyToOne
    private Shop shop;
    /// 品牌
    @ManyToOne
    private Brand brand;
    /// 分类
    @ManyToOne
    private Category category;
    /// 产品属性
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @OrderBy("id")
    private List<ProductAttribute> attributes;
    /// 产品变体
    @OneToMany(mappedBy = "product", cascade = CascadeType.MERGE)
    @OrderBy("id")
    private List<ProductVariant> variants;

    public enum Type {
        /// 实体商品
        PHYSICAL,
        /// 虚拟商品
        VIRTUAL
    }
}
