package cool.houge.mahu.entity.system;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/// 系统安全配置
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "secret", schema = "system")
public class Secret {

    /// 键
    ///
    /// - `wechat.pay.apiv3.key` 微信支付 APIv3 密钥
    @Id
    private String key;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 数据版本（乐观锁）
    @Version
    private int ver;
    /// 值
    private String value;
}
