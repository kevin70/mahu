package cool.houge.mahu.entity.sys;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/// 延迟消息
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys")
public class DelayMessage {

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
    /// 消息主题
    private String topic;
    /// 状态
    private Integer status;
    /// 消息延迟到的绝对时间（精确到毫秒）
    private Instant delayUntil;
    /// 初始重试间隔（1000 毫秒，即 1 秒）
    private Integer delay;
    /// 重试间隔的倍数（指数退避策略，间隔依次为 1s、2s、4s、8s...）
    private Integer multiplier;
    /// 最大重试间隔（10000 毫秒，即 10 秒，避免间隔无限增大）
    private Integer maxDelay;
    /// 最大重试次数（包括首次执行）
    private Integer maxAttempts;
    /// 已重试次数
    private Integer retryCount;
    /// 消息内容存储业务所需的所有数据
    private String body;
}
