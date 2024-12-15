package cool.houge.mahu.entity.market;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 图片、视频资源
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "asset", schema = "market")
public class Asset {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 资源 URI
    private String uri;
    /// 所属商店
    @ManyToOne
    private Shop shop;
}
