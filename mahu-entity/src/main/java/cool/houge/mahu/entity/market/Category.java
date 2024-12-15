package cool.houge.mahu.entity.market;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 商品分类
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "category", schema = "market")
public class Category {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 更新时间
    @WhenModified
    private Instant updateTime;
    /// 品牌名称
    private String name;
    /// 父分类
    @OneToOne
    private Category parent;
    /// 排序
    private Integer ordering;
    /// 备注
    private String remark;
}
