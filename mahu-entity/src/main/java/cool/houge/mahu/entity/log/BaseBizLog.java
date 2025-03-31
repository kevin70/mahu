package cool.houge.mahu.entity.log;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/// 业务日志基类
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@MappedSuperclass
public abstract class BaseBizLog {

    /// 日志追踪 ID。
    ///
    /// Time-Sorted Unique Identifiers [TSID](https://github.com/f4b6a3/tsid-creator)。
    @Id
    protected Long id;

    /// 创建时间
    @WhenCreated
    protected LocalDateTime createdAt;

}
