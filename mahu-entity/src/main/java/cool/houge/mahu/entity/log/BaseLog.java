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
public abstract class BaseLog {

    /// 日志追踪 ID
    ///
    /// [ULID](https://github.com/ulid/spec)
    @Id
    protected String id;

    /// 创建时间
    @WhenCreated
    protected LocalDateTime createdAt;

}
