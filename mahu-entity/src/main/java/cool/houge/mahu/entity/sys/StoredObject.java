package cool.houge.mahu.entity.sys;

import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

/// 存储对象
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "object")
public class StoredObject {

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
    /// 证件类型
    @Enumerated(EnumType.ORDINAL)
    private Type type;
    /// 状态
    private Integer status;
    /// 对象的完整 key（含前缀）
    private String objectKey;
    /// 可以用来存储额外的元数据，比如图片尺寸、背景颜色、格式等
    @DbJsonB
    private Map<String, Object> metadata;

    /// 资源类型
    public enum Type {
        /// `0`管理员头像
        ADMIN_AVATAR("p/admin/avatars"),
        ;

        /// OSS 存储目录前缀
        @Getter
        private final String prefix;

        Type(String prefix) {
            this.prefix = prefix;
        }

        /// 构建对象名称
        public String buildObjectName(@NonNull String filename, @NonNull String ext) {
            return prefix + "/" + filename + "." + ext;
        }
    }
}
