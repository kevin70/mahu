package cool.houge.mahu.entity;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 品牌
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "brand")
public class Brand {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 品牌名称
    private String name;
    /// 品牌 LOGO
    private String logo;
    /// 品牌首字母
    private String firstLetter;
    /// 排序值
    private Integer ordering;
}
