package cool.houge.mahu.entity.sys;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/// 证件照
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys")
public class IdPhoto {

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
    /// 用户 ID
    private Long uid;
    /// 业务 ID
    private Long bizId;
    /// 证件类型
    @Enumerated(EnumType.ORDINAL)
    private Type type;
    /// 状态
    private Integer status;
    /// OSS 对象名称
    private String objectName;
    /// 有效期开始日期
    private LocalDate validFrom;
    /// 有效期结束日期（值为空表示永久）
    private LocalDate validTo;
    /// 证件号码
    private String idNum;
    /// 背景色
    private String backgroundColor;

    public enum Type {
    //
    }
}
