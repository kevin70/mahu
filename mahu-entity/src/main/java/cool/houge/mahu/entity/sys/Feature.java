package cool.houge.mahu.entity.sys;

import com.google.common.collect.ImmutableList;
import cool.houge.mahu.entity.Auditable;
import cool.houge.mahu.util.RoaringBitmapUtils;
import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

/// 系统功能
@Getter
@Setter
@Entity
@Table(schema = "sys")
public class Feature implements Auditable {

    /// 主键
    @Id
    private Integer id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 模块
    private String module;
    /// 代码
    private String code;
    /// 名称
    private String name;
    /// 描述
    private String description;
    /// 状态
    ///
    /// - [cool.houge.mahu.StatusCodes#DRAFT]
    /// - [cool.houge.mahu.StatusCodes#ACTIVE]
    /// - [cool.houge.mahu.StatusCodes#DISABLED]
    /// - [cool.houge.mahu.StatusCodes#ARCHIVED]
    private Integer status;
    /// 生效开始时间（精确到秒）
    private LocalDateTime effectiveFrom;
    /// 生效结束时间（精确到秒）
    private LocalDateTime effectiveTo;
    /// 每天的开始时间（精确到秒）
    private LocalTime startTime;
    /// 每天的结束时间（精确到秒，结束时间小于开始时间代表跨天）
    private LocalTime endTime;
    /// 启用的星期
    @DbJsonB
    private List<Integer> weekdays;
    /// 可用的用户
    private byte[] allowUserRb;
    /// 禁用的用户
    private byte[] denyUserRb;
    /// 扩展属性
    private Map<String, Object> extraProperties;
    /// 扩展属性 JSON Schema
    private Map<String, Object> extraSchema;

    public Feature setAllowUsers(List<Long> list) {
        return setRb(list, this::setAllowUserRb);
    }

    public List<Long> getAllowUsers() {
        return toList(allowUserRb);
    }

    public Feature setDenyUsers(List<Long> list) {
        return setRb(list, this::setDenyUserRb);
    }

    public List<Long> getDenyUsers() {
        return toList(denyUserRb);
    }

    private Feature setRb(List<Long> list, Consumer<byte[]> c) {
        var rb = new Roaring64NavigableMap();
        list.forEach(rb::add);
        c.accept(RoaringBitmapUtils.toBytes(rb));
        return this;
    }

    private List<Long> toList(byte[] bytes) {
        var rb = RoaringBitmapUtils.toRoaring64NavigableMap(bytes);
        var b = ImmutableList.<Long>builder();
        rb.forEach(b::add);
        return b.build();
    }
}
