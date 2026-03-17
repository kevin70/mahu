package cool.houge.mahu.entity;

import io.ebean.annotation.ChangeLog;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/// 用户资产
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@ChangeLog(
        updatesThatInclude = {
            "balance",
            "frozen",
            "totalIn",
            "totalOut"
        })
@Entity
public class UserAsset {

    /// 主键
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
    @Enumerated
    private Kind kind;
    /// 以最小计量单位记录（例如分、分/最小积分单位）
    private Long balance;
    /// 冻结/占用的数量（仍计入总量但不可用）
    private Long frozen;
    /// 累计入账（正向变动的总和，单位与 balance 相同）
    private Long totalIn;
    /// 累计出账（负向变动的绝对值总和）
    private Long totalOut;

    /// 资产类型
    public enum Kind {
        /// 橙子
        ORANGE
    }
}
