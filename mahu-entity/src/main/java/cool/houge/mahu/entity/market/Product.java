package cool.houge.mahu.entity.market;

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
@Table(name = "product", schema = "market")
public class Product {

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
    private Boolean deleted;
    /// 数据版本
    @Version
    private Integer ver;
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
    /// 商店 SLUG
    private String shopSlug;
    /// 品牌
    @ManyToOne
    private Brand brand;
    /// 分类
    @ManyToOne
    private Category category;
    /// 产品属性
    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private List<ProductAttribute> productAttributes;
    /// 产品变体
    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private List<ProductVariant> productVariants;

    public enum Type {
        /// 无
        NONE,
        /// 实体商品
        PHYSICAL,
        /// 虚拟商品
        VIRTUAL
    }
}
