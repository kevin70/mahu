package cool.houge.mahu;

import lombok.experimental.UtilityClass;

/// 状态码定义
@UtilityClass
public final class StatusCodes {

    /// 草稿
    ///
    /// 未正式提交、可编辑的临时状态，通常用于需要多次修改才能正式提交的数据记录
    public static final int DRAFT = 10;

    /// 待处理/待审核/待确认
    ///
    /// 已提交但未处理，需等待系统/人工操作（如审核、支付、验证）
    public static final int PENDING = 11;

    /// 已批准/已通过/已授权
    ///
    /// 通常为审核流程的终态，需经过系统或人工的正式确认。对立状态: `REJECTED` `PENDING`
    public static final int APPROVED = 20;

    /// 活跃
    ///
    /// 已生效，可正常使用（如已支付订单、已认证用户）
    public static final int ACTIVE = 22;

    /// 已上架
    ///
    /// 可公开销售
    public static final int PUBLISHED = 26;

    /// 已下架
    ///
    /// 临时停止销售
    public static final int UNLISTED = 27;

    /// 已支付
    ///
    /// `PAID`是业务系统中表示支付已完成的核心状态，主要应用于订单、交易、订阅等涉及支付的业务场景
    public static final int PAID = 30;

    /// 部分支付
    ///
    /// 支付系统中的特殊状态，表示订单或交易只完成了部分金额的支付，常见于分期付款、定金尾款等场景
    public static final int PARTIAL_PAID = 31;

    /// 已退款
    ///
    /// 全额退款完成
    public static final int REFUNDED = 36;

    /// 部分退款
    ///
    /// 部分商品/金额退款
    public static final int PARTIAL_REFUND = 37;

    /// 处理中/进行中
    ///
    /// `PROCESSING`是业务系统中表示请求已接收并正在处理，但尚未完成的中间状态，通常出现在异步操作、支付、工单系统等需要时间执行的场景
    public static final int PROCESSING = 50;

    /// 被拒绝
    ///
    /// 系统中表示请求或操作因不符合规则被明确拒绝的终结状态，强调业务规则层面的否定判断，通常需要给出具体拒绝原因
    public static final int REJECTED = 72;

    /// 锁定
    ///
    /// 安全原因临时限制
    public static final int LOCKED = 74;

    /// 非活跃
    ///
    /// 表示资源或账户处于休眠状态但数据完整保留的中间状态，通常由不活跃触发而非主动禁用
    public static final int INACTIVE = 75;

    /// 已禁用
    ///
    /// 管理员或用户主动限制
    public static final int DISABLED = 76;

    /// 冻结
    ///
    /// 表示资源或账户被临时锁定且保留数据的特殊状态，介于活跃状态与永久终止状态之间
    public static final int FROZEN = 77;

    /// 已完成/已完结
    ///
    /// 流程的最终状态（不可再自动流转）
    public static final int COMPLETED = 88;

    /// 失败
    ///
    /// 系统中最常见的终结状态之一，表示某个操作或流程因意外情况未能完成，通常需要错误处理机制介入
    public static final int FAILED = 89;

    /// 已过期
    ///
    /// 系统中表示超过有效期限自动失效的终止状态，通常用于有时间限制的业务场景，由时间条件自动触发（非人工操作）
    public static final int EXPIRED = 90;

    /// 已取消
    ///
    /// 业务系统中表示用户或系统主动中止流程的终止状态，与自动触发的终止状态（如EXPIRED/TERMINATED）不同，强调主观取消行为
    public static final int CANCELLED = 91;

    /// 已封锁
    ///
    /// 系统自动防护机制，由风控规则自动触发，需要额外验证才能解除，通常记录安全日志
    public static final int BLOCKED = 92;

    /// 软删除
    ///
    /// 逻辑删除，通常不再显示给用户，只能通过管理员恢复
    public static final int DELETED = 93;

    /// 归档
    ///
    /// 归档状态，数据已移动到历史库，业务系统不再使用
    public static final int ARCHIVED = 95;

    /// 作废
    ///
    /// - 订单作废：比如订单录入错误，需要作废，然后重新下单。
    /// - 发票作废：开错发票，需要作废重开。
    /// - 合同作废：因某些原因合同无效，需要作废。
    public static final int VOIDED = 96;

    /// 临时冻结
    ///
    /// （罕见）最高级别的强制措施，针对严重违规或法律要求
    public static final int SUSPENDED = 97;

    /// 已终止
    ///
    /// （罕见）已终止/永久关闭，不可逆性：无法通过常规手段恢复，触发方式：人工执行或自动合同到期
    public static final int TERMINATED = 99;
}
