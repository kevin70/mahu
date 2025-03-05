package cool.houge.mahu.entity.mart;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/// 商品分类
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "category", schema = "mart")
public class Category {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 创建时间
    @WhenCreated
    private LocalDateTime createdAt;
    /// 更新时间
    @WhenModified
    private LocalDateTime updatedAt;
    /// 品牌名称
    private String name;
    /// 父分类
    @OneToOne
    private Category parent;
    /// 排序
    private Integer ordering;
    /// 备注
    private String remark;

    /// 分类子项
    private transient List<Category> children;
}
