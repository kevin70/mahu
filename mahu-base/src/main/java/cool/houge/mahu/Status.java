package cool.houge.mahu;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

/// 系统状态码枚举
///
/// 状态码分组规则：
/// - 10-19: 初始状态
/// - 20-49: 正向业务状态
/// - 50-79: 中间/限制状态
/// - 80-99: 终态/异常状态
public enum Status {

    /* ==================== 初始状态 (10-19) ==================== */

    /// 草稿
    ///
    /// 未正式提交、可编辑的临时状态，通常用于需要多次修改才能正式提交的数据记录
    DRAFT(10, "草稿", StatusGroup.INITIAL),

    /// 待处理/待审核/待确认
    ///
    /// 已提交但未处理，需等待系统/人工操作（如审核、支付、验证）
    PENDING(11, "待处理", StatusGroup.INITIAL),

    /* ==================== 正向业务状态 (20-49) ==================== */

    /// 已批准/已通过/已授权
    ///
    /// 通常为审核流程的终态，需经过系统或人工的正式确认。对立状态: `REJECTED` `PENDING`
    APPROVED(20, "已批准", StatusGroup.POSITIVE),

    /// 活跃
    ///
    /// 已生效，可正常使用（如已支付订单、已认证用户）
    ACTIVE(22, "活跃", StatusGroup.POSITIVE),

    /// 已上架
    ///
    /// 可公开销售
    PUBLISHED(26, "已上架", StatusGroup.POSITIVE),

    /// 已下架
    ///
    /// 临时停止销售
    UNLISTED(27, "已下架", StatusGroup.POSITIVE),

    /// 已支付
    ///
    /// `PAID`是业务系统中表示支付已完成的核心状态，主要应用于订单、交易、订阅等涉及支付的业务场景
    PAID(30, "已支付", StatusGroup.POSITIVE),

    /// 部分支付
    ///
    /// 支付系统中的特殊状态，表示订单或交易只完成了部分金额的支付，常见于分期付款、定金尾款等场景
    PARTIAL_PAID(31, "部分支付", StatusGroup.POSITIVE),

    /// 已退款
    ///
    /// 全额退款完成
    REFUNDED(36, "已退款", StatusGroup.POSITIVE),

    /// 部分退款
    ///
    /// 部分商品/金额退款
    PARTIAL_REFUNDED(37, "部分退款", StatusGroup.POSITIVE),

    /// 已发货
    ///
    /// 物流已发出（含物流单号）
    SHIPPED(40, "已发货", StatusGroup.POSITIVE),

    /// 已送达
    ///
    /// 物流显示签收（可能未实际收货）
    DELIVERED(48, "已送达", StatusGroup.POSITIVE),

    /* ==================== 中间/限制状态 (50-79) ==================== */

    /// 处理中/进行中
    ///
    /// `PROCESSING`是业务系统中表示请求已接收并正在处理，但尚未完成的中间状态，
    /// 通常出现在异步操作、支付、工单系统等需要时间执行的场景
    PROCESSING(50, "进行中", StatusGroup.INTERMEDIATE),

    /// 已确认
    ///
    /// 用户已确认收到货
    CONFIRMED(61, "已确认", StatusGroup.INTERMEDIATE),

    /// 被拒绝
    ///
    /// 系统中表示请求或操作因不符合规则被明确拒绝的终结状态，强调业务规则层面的否定判断，
    /// 通常需要给出具体拒绝原因
    REJECTED(72, "被拒绝", StatusGroup.INTERMEDIATE),

    /// 暂停/挂起
    ///
    /// 临时暂停处理，等待外部条件满足后恢复
    SUSPENDED(73, "暂停", StatusGroup.INTERMEDIATE),

    /// 锁定
    ///
    /// 安全原因临时限制
    LOCKED(74, "锁定", StatusGroup.INTERMEDIATE),

    /// 非活跃
    ///
    /// 表示资源或账户处于休眠状态但数据完整保留的中间状态，通常由不活跃触发而非主动禁用
    INACTIVE(75, "非活跃", StatusGroup.INTERMEDIATE),

    /// 已禁用
    ///
    /// 管理员或用户主动限制
    DISABLED(76, "已禁用", StatusGroup.INTERMEDIATE),

    /// 冻结
    ///
    /// 表示资源或账户被临时锁定且保留数据的特殊状态，介于活跃状态与永久终止状态之间
    FROZEN(77, "冻结", StatusGroup.INTERMEDIATE),

    /* ==================== 终态/异常状态 (80-99) ==================== */

    /// 作废
    ///
    /// - 订单作废：比如订单录入错误，需要作废，然后重新下单。
    /// - 发票作废：开错发票，需要作废重开。
    /// - 合同作废：因某些原因合同无效，需要作废。
    VOIDED(87, "作废", StatusGroup.TERMINAL),

    /// 已完成/已完结
    ///
    /// 流程的最终状态（不可再自动流转）
    COMPLETED(88, "已完成", StatusGroup.TERMINAL),

    /// 失败
    ///
    /// 系统中最常见的终结状态之一，表示某个操作或流程因意外情况未能完成，
    /// 通常需要错误处理机制介入
    FAILED(89, "失败", StatusGroup.TERMINAL),

    /// 已过期
    ///
    /// 系统中表示超过有效期限自动失效的终止状态，通常用于有时间限制的业务场景，
    /// 由时间条件自动触发（非人工操作）
    EXPIRED(90, "已过期", StatusGroup.TERMINAL),

    /// 已取消
    ///
    /// 业务系统中表示用户或系统主动中止流程的终止状态，与自动触发的终止状态
    /// （如EXPIRED/TERMINATED）不同，强调主观取消行为
    CANCELLED(91, "已取消", StatusGroup.TERMINAL),

    /// 已封锁
    ///
    /// 系统自动防护机制，由风控规则自动触发，需要额外验证才能解除，通常记录安全日志
    BLOCKED(92, "已封锁", StatusGroup.TERMINAL),

    /// 软删除
    ///
    /// 逻辑删除，通常不再显示给用户，只能通过管理员恢复
    DELETED(93, "软删除", StatusGroup.TERMINAL),

    /// 归档
    ///
    /// 归档状态，数据已移动到历史库，业务系统不再使用
    ARCHIVED(95, "归档", StatusGroup.TERMINAL),

    /// 已终止
    ///
    /// （罕见）已终止/永久关闭，不可逆性：无法通过常规手段恢复，触发方式：人工执行。
    TERMINATED(99, "已终止", StatusGroup.TERMINAL);

    /* ==================== 枚举属性 ==================== */

    /// 代码值
    @Getter
    private final int code;
    /// 标签
    @Getter
    private final String label;
    /// 分组
    @Getter
    private final StatusGroup group;

    /* ==================== 静态映射 ==================== */

    private static final Map<Integer, Status> CODE_MAP = new HashMap<>();

    static {
        for (Status status : Status.values()) {
            CODE_MAP.put(status.code, status);
        }
    }

    /* ==================== 构造方法 ==================== */

    Status(int code, String label, StatusGroup group) {
        this.code = code;
        this.label = label;
        this.group = group;
    }

    /* ==================== 状态组枚举 ==================== */

    public enum StatusGroup {
        /// 初始状态 (10-19)
        INITIAL("初始状态"),

        /// 正向业务状态 (20-49)
        POSITIVE("正向业务状态"),

        /// 中间/限制状态 (50-79)
        INTERMEDIATE("中间/限制状态"),

        /// 终态/异常状态 (80-99)
        TERMINAL("终态/异常状态");

        @Getter
        private final String description;

        StatusGroup(String description) {
            this.description = description;
        }
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
        return CODE_MAP.containsKey(code);
    }

    /// 判断两个状态码是否相等
    public boolean eq(Integer code) {
        return code != null && this.code == code;
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
