package cool.houge.mahu.entity.sys;

import io.ebean.annotation.ChangeLog;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/// 功能开关
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "feature_flags")
@ChangeLog(
        updatesThatInclude = {
            "name",
            "description",
            "enabled",
            "preset",
            "enableAt",
            "disableAt",
            "ordering"
        })
public class FeatureFlag {

    /// 主键，自增
    @Id
    private Integer id;

    /// 全局唯一标识，程序通过此字段读取开关；命名规范：{module}.{feature}，如 payment.wechat
    private String code;

    /// 可读名称，如"微信支付"
    private String name;

    /// 开关用途说明，建议描述：功能用途 + 关闭后的影响
    private String description;

    /// 开关状态：true=开启，false=关闭，修改后直接生效
    private boolean enabled;

    /// 系统预置标志：true=内置开关，禁止删除
    private boolean preset;

    /// 定时开启时间，NULL=不启用定时；定时任务到期后置 enabled=true 并清空此字段
    private LocalDateTime enableAt;

    /// 定时关闭时间，NULL=不启用定时；定时任务到期后置 enabled=false 并清空此字段
    private LocalDateTime disableAt;

    /// 排序权重，值越大越靠前，后台管理页展示顺序依据
    private Integer ordering;

    /// 创建时间
    @WhenCreated
    private LocalDateTime createdAt;

    /// 更新时间
    @WhenModified
    private LocalDateTime updatedAt;
}
