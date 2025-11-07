package cool.houge.mahu.entity.sys;

import cool.houge.mahu.CodedEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
    /// 乐观锁
    @Version
    private Integer ver;
    /// 消息主题
    private Topic topic;
    /// 状态
    private Status status;
    /// 消息延迟到的绝对时间（精确到毫秒）
    private Instant delayUntil;
    /// 消息内容存储业务所需的所有数据
    private String body;
    /// 已重试次数
    private Integer retryCount;
    /// 最大重试次数
    private Integer maxRetryCount;

    /// 消息主题
    public enum Topic {
    //
    }

    /// 状态
    public enum Status implements CodedEnum {
        /// 待执行
        PENDING(11),
        /// 执行中
        PROCESSING(50),
        /// 执行成功
        COMPLETED(88),
        /// 执行失败
        FAILED(89);

        private final int code;

        Status(int code) {
            this.code = code;
        }

        @DbEnumValue(storage = DbEnumType.INTEGER)
        @Override
        public int getCode() {
            return code;
        }
    }
}
