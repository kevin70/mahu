package cool.houge.mahu.entity;

import io.ebean.annotation.ChangeLog;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/// 预占/锁定表（用于支付流程中临时占用）
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@ChangeLog(
        updatesThatInclude = {
            "status",
            "reservedAmount",
            "referenceId",
            "featureId"
        })
@Entity
public class AssetReservation {

    /// 流水号
    @Id
    private UUID id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 用户 ID
    private Long uid;
    /// 资产类型
    private Integer kind;
    /// 占用金额
    private Long reservedAmount;
    /// 数据状态
    ///
    /// - [cool.houge.mahu.Status#PENDING]
    /// - [cool.houge.mahu.Status#COMPLETED]
    /// - [cool.houge.mahu.Status#EXPIRED]
    /// - [cool.houge.mahu.Status#CANCELLED]
    private Integer status;
    /// 关联业务单号，如订单id
    private String referenceId;
    /// 功能标识
    private Integer featureId;
}
