package cool.houge.mahu.entity.mart;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 商店
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "shop", schema = "mart")
public class Shop {

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
    /// SLUG
    private String slug;
    /// 描述
    private String description;
}
