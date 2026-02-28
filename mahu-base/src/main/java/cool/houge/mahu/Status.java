package cool.houge.mahu;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

/// 系统状态码枚举
///
/// 定义了系统中各种状态的标准化编码和描述，采用数字编码体系便于状态管理和查询
/// 编码规则：
/// - 1x: 初始/草稿/待处理状态
/// - 2x: 正向/已生效状态
/// - 3x: 支付/退款状态
/// - 7x: 限定范围状态
/// - 8x: 终结/历史状态
///
/// @author ZY (kzou227@qq.com)
@Getter
@AllArgsConstructor
public enum Status {

    // ========== 1x 初始/草稿/待处理 ==========
    /// 草稿（临时未正式提交）
    DRAFT(10, "草稿"),
    /// 待处理（如待审核、待确认）
    PENDING(11, "待处理"),
    /// 暂停/挂起
    SUSPENDED(12, "暂停"),
    /// 处理中/进行中（如第三方回调、长时处理）
    PROCESSING(16, "进行中"),

    // ========== 2x 正向/已生效 ==========
    /// 已批准/通过
    APPROVED(20, "已批准"),
    /// 活跃/生效
    ACTIVE(22, "活跃"),

    // ========== 3x 支付/退款状态 ==========
    /// 部分支付
    PARTIAL_PAID(30, "部分支付"),
    /// 已支付
    PAID(31, "已支付"),
    /// 部分退款
    PARTIAL_REFUNDED(32, "部分退款"),
    /// 已退款
    REFUNDED(33, "已退款"),

    // ========== 7x 限定范围 ==========
    /// 锁定（安全限制）
    LOCKED(72, "锁定"),
    /// 已禁用（主动限制）
    DISABLED(74, "已禁用"),

    // ========== 8x 终结/历史 ==========
    /// 被拒绝
    REJECTED(86, "被拒绝"),
    /// 作废
    VOIDED(87, "作废"),
    /// 已完成
    COMPLETED(88, "已完成"),
    /// 失败
    FAILED(89, "失败"),
    /// 已过期
    EXPIRED(90, "已过期"),
    /// 非活跃
    INACTIVE(92, "非活跃"),
    /// 已取消
    CANCELLED(93, "已取消"),
    /// 已删除（逻辑删除）
    DELETED(95, "已删除"),
    /// 归档
    ARCHIVED(97, "归档"),
    /// 已终止
    TERMINATED(99, "已终止");

    /// 状态码值
    private final int code;
    /// 状态标签描述
    private final String label;

    /// 状态码到枚举的映射表，用于快速查找
    private static final Map<Integer, Status> CODE_MAP = new HashMap<>();

    static {
        for (Status status : Status.values()) {
            CODE_MAP.put(status.code, status);
        }
    }

    /// 根据状态码获取枚举实例
    ///
    /// @param code 状态码
    /// @return 包含枚举实例的Optional，如果状态码无效则返回空Optional
    public static Optional<Status> fromCode(Integer code) {
        return Optional.ofNullable(CODE_MAP.get(code));
    }

    /// 根据状态码获取枚举实例，如果不存在则抛出异常
    ///
    /// @param code 状态码
    /// @return 对应的状态枚举实例
    /// @throws IllegalArgumentException 当状态码无效时抛出
    public static Status valueOf(Integer code) {
        Status status = CODE_MAP.get(code);
        if (status == null) {
            throw new IllegalArgumentException("无效的状态码: " + code);
        }
        return status;
    }

    /// 判断状态码是否有效
    ///
    /// @param code 状态码
    /// @return 如果状态码有效返回true，否则返回false
    public static boolean isValidCode(Integer code) {
        return code != null && CODE_MAP.containsKey(code);
    }

    /// 判断当前状态码是否与指定状态码相等
    ///
    /// @param code 要比较的状态码
    /// @return 如果相等返回true，否则返回false
    public boolean eq(Integer code) {
        return Objects.equals(this.code, code);
    }

    /// 判断当前状态码是否与指定状态码不相等
    ///
    /// @param code 要比较的状态码
    /// @return 如果不相等返回true，否则返回false
    public boolean neq(Integer code) {
        return !eq(code);
    }

    /// 返回状态的字符串表示
    ///
    /// 格式为：状态名称(状态码)，例如：ACTIVE(25)
    ///
    /// @return 状态的字符串表示
    @Override
    public String toString() {
        return String.format("%s(%d)", this.name(), this.code);
    }
}
