package cool.houge.mahu.entity;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/// 资产流水表（不可变账本，记录每一次变动）
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
public class AssetTransaction {

    /// 流水号
    @Id
    private UUID id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 用户 ID
    private Long uid;
    /// 资产类型
    private Integer kind;
    /// 正为增加，负为减少（最小单位）
    private Long changeAmount;
    /// 标明这条流水是“入（credit）”还是“出（debit）”
    @Enumerated(EnumType.STRING)
    private Direction direction;
    /// 数据状态
    private Integer status;
    /// 关联业务单号，如订单id
    private String referenceId;
    /// 幂等 key（避免重复执行）
    private String idempotentKey;
    /// 功能标识
    private Integer featureId;

    /// 标明这条流水是“入（credit）”还是“出（debit）”
    public enum Direction {
        /// 无
        none,
        /// 入
        credit,
        /// 出
        debit,
    }
}
