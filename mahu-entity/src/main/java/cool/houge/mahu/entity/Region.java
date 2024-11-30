package cool.houge.mahu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/// 行政地区
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "region")
public class Region {

    /// 行政代码
    @Id
    private String code;
    /// 地区名称
    private String name;
    /// 深度
    private Integer depth;
    /// 父行政区代码
    private String parentCode;

    // ============================================================ //
    /// 子元素
    @Transient
    private List<Region> children;
}
