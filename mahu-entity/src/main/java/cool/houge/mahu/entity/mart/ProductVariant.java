package cool.houge.mahu.entity.mart;

import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/// 产品变体
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "product_variant", schema = "mart")
public class ProductVariant {

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
    /// 数据版本
    @Version
    private Integer ver;
    /// 产品状态
    @Enumerated(EnumType.ORDINAL)
    private ProductStatus status;
    /// 产品
    @ManyToOne
    private Product product;
    /// 产品限定名
    ///
    /// 相同的产品下限定名唯一
    private String qn;
    /// 封面
    private String cover;
    /// 价格
    private BigDecimal price;
    /// 长度
    private Integer length;
    /// 宽度
    private Integer width;
    /// 高度
    private Integer height;
    /// 重量
    private Integer weight;
    /// 产品变体属性
    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL)
    @OrderBy("id")
    private List<ProductVariantAttribute> attributes;
}
