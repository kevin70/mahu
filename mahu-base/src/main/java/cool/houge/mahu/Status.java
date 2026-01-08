package cool.houge.mahu;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

/// 系统状态码枚举
///
/// @author ZY (kzou227@qq.com)
@AllArgsConstructor
@Getter
public enum Status {

    // ========== 1x 初始/草稿/待处理 ==========
    /** 草稿（临时未正式提交） */
    DRAFT(10, "草稿", StatusGroup.INITIAL),
    /** 待处理（如待审核、待确认） */
    PENDING(11, "待处理", StatusGroup.INITIAL),

    // ========== 2x 正向/已生效 ==========
    /** 已批准/通过 */
    APPROVED(20, "已批准", StatusGroup.ACTIVE),
    /** 活跃/生效 */
    ACTIVE(22, "活跃", StatusGroup.ACTIVE),
    /** 已支付 */
    PAID(30, "已支付", StatusGroup.ACTIVE),
    /** 部分支付 */
    PARTIAL_PAID(31, "部分支付", StatusGroup.ACTIVE),
    /** 已退款 */
    REFUNDED(36, "已退款", StatusGroup.FINAL),
    /** 部分退款 */
    PARTIAL_REFUNDED(37, "部分退款", StatusGroup.FINAL),
    /** 已发货 */
    SHIPPED(40, "已发货", StatusGroup.ACTIVE),
    /** 已送达 */
    DELIVERED(48, "已送达", StatusGroup.ACTIVE),

    // ========== 5x 处理中/限定/等待 ==========
    /** 处理中/进行中（如第三方回调、长时处理） */
    PROCESSING(50, "进行中", StatusGroup.LIMITED),
    /** 暂停/挂起 */
    SUSPENDED(51, "暂停", StatusGroup.LIMITED),
    /** 锁定（安全限制） */
    LOCKED(52, "锁定", StatusGroup.LIMITED),
    /** 非活跃 */
    INACTIVE(53, "非活跃", StatusGroup.LIMITED),
    /** 已禁用（主动限制） */
    DISABLED(54, "已禁用", StatusGroup.LIMITED),

    // ========== 8x 终结/历史 ==========
    /** 被拒绝 */
    REJECTED(86, "被拒绝", StatusGroup.FINAL),
    /** 作废 */
    VOIDED(87, "作废", StatusGroup.FINAL),
    /** 已完成 */
    COMPLETED(88, "已完成", StatusGroup.FINAL),
    /** 失败 */
    FAILED(89, "失败", StatusGroup.FINAL),
    /** 已过期 */
    EXPIRED(90, "已过期", StatusGroup.FINAL),
    /** 已取消 */
    CANCELLED(91, "已取消", StatusGroup.FINAL),
    /** 已删除（逻辑删除） */
    DELETED(93, "已删除", StatusGroup.FINAL),
    /** 归档 */
    ARCHIVED(95, "归档", StatusGroup.FINAL),
    /** 已终止 */
    TERMINATED(99, "已终止", StatusGroup.FINAL);

    /// 代码值
    private final int code;
    /// 标签
    private final String label;
    /// 分组
    private final StatusGroup group;

    /* ==================== 静态映射 ==================== */

    private static final Map<Integer, Status> CODE_MAP = new HashMap<>();

    static {
        for (Status status : Status.values()) {
            CODE_MAP.put(status.code, status);
        }
    }

    /* ==================== 状态组枚举 ==================== */

    @AllArgsConstructor
    @Getter
    public enum StatusGroup {
        /** 初始状态 */
        INITIAL("初始状态"),
        /** 活跃状态 */
        ACTIVE("正常/活跃状态"),
        /** 限制/处理中间态 */
        LIMITED("处理中/限制状态"),
        /** 终结/异常状态 */
        FINAL("终结/异常状态");

        private final String description;
    }

    /* ==================== 静态工具方法 ==================== */

    /// 根据状态码获取枚举实例
    public static Optional<Status> fromCode(Integer code) {
        return Optional.ofNullable(CODE_MAP.get(code));
    }

    /// 根据状态码获取枚举实例，如果不存在则抛出异常
    public static Status valueOf(Integer code) {
        Status status = CODE_MAP.get(code);
        if (status == null) {
            throw new IllegalArgumentException("无效的状态码: " + code);
        }
        return status;
    }

    /// 判断状态码是否有效
    public static boolean isValidCode(Integer code) {
        return code != null && CODE_MAP.containsKey(code);
    }

    /// 判断两个状态码是否相等
    public boolean eq(Integer code) {
        return Objects.equals(this.code, code);
    }

    /// 判断两个状态码是否不相等
    public boolean neq(Integer code) {
        return !eq(code);
    }

    @Override
    public String toString() {
        return String.format("%s(%d)", this.name(), this.code);
    }
}
