package cool.houge.mahu.config;

import cool.houge.mahu.CodedEnum;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

/// 系统状态码枚举
///
/// 定义了系统中各种状态的标准化编码和描述，采用三位数全局唯一编码体系便于状态管理和查询。
/// 编码规则：
/// - 100-199: 初始/等待态
/// - 200-299: 通用进行态
/// - 300-399: 交易/支付态
/// - 400-499: 履约/物流态
/// - 700-799: 限制态
/// - 800-899: 正常终态
/// - 900-999: 异常/失效终态
///
/// @author ZY (kzou227@qq.com)
@Getter
@AllArgsConstructor
public enum Status implements CodedEnum {

    // ========== 100-199 初始/等待态 ==========
    /// 草稿（临时未正式提交）
    DRAFT(100, "草稿"),
    /// 已创建
    CREATED(110, "已创建"),
    /// 已提交
    SUBMITTED(120, "已提交"),
    /// 待处理（如待审核、待确认）
    PENDING(130, "待处理"),
    /// 待确认
    PENDING_CONFIRMATION(170, "待确认"),
    /// 待审核
    PENDING_REVIEW(180, "待审核"),
    /// 待支付
    PENDING_PAYMENT(190, "待支付"),

    // ========== 200-299 通用进行态 ==========
    /// 活跃/生效
    ACTIVE(200, "活跃"),
    /// 已批准/通过
    APPROVED(210, "已批准"),
    /// 处理中
    PROCESSING(220, "处理中"),
    /// 已确认
    CONFIRMED(230, "已确认"),
    /// 已接受
    ACCEPTED(240, "已接受"),
    /// 已分配
    ASSIGNED(250, "已分配"),
    /// 可用
    AVAILABLE(260, "可用"),
    /// 已发布
    PUBLISHED(270, "已发布"),
    /// 运行中
    RUNNING(280, "运行中"),
    /// 重试中
    RETRYING(290, "重试中"),

    // ========== 300-399 交易/支付态 ==========
    /// 已支付
    PAID(300, "已支付"),
    /// 部分支付
    PARTIALLY_PAID(310, "部分支付"),
    /// 退款中
    REFUNDING(320, "退款中"),
    /// 已退款
    REFUNDED(330, "已退款"),
    /// 部分退款
    PARTIALLY_REFUNDED(340, "部分退款"),

    // ========== 400-499 履约/物流态 ==========
    /// 已打包（商品已打包完成）
    PACKAGED(400, "已打包"),
    /// 已发货（商品已发出）
    SHIPPED(410, "已发货"),
    /// 运输中（商品在运输途中）
    IN_TRANSIT(420, "运输中"),
    /// 已送达（商品已送达）
    DELIVERED(430, "已送达"),

    // ========== 700-799 限制态 ==========
    /// 锁定（安全限制）
    LOCKED(700, "锁定"),
    /// 暂停/挂起
    SUSPENDED(710, "暂停"),
    /// 已禁用（主动限制）
    DISABLED(720, "已禁用"),
    /// 已冻结
    FROZEN(730, "已冻结"),
    /// 已阻断
    BLOCKED(740, "已阻断"),
    /// 非活跃
    INACTIVE(750, "非活跃"),

    // ========== 800-899 正常终态 ==========
    /// 已完成
    COMPLETED(800, "已完成"),
    /// 归档
    ARCHIVED(810, "归档"),
    /// 已关闭
    CLOSED(820, "已关闭"),
    /// 已结算
    SETTLED(830, "已结算"),
    /// 已履约
    FULFILLED(840, "已履约"),

    // ========== 900-999 异常/失效终态 ==========
    /// 失败
    FAILED(900, "失败"),
    /// 被拒绝
    REJECTED(910, "被拒绝"),
    /// 已过期
    EXPIRED(920, "已过期"),
    /// 已取消
    CANCELLED(930, "已取消"),
    /// 作废
    VOIDED(940, "作废"),
    /// 已删除（逻辑删除）
    DELETED(950, "已删除"),
    /// 已终止
    TERMINATED(960, "已终止"),
    /// 已回滚
    ROLLED_BACK(970, "已回滚"),
    /// 已超时
    TIMED_OUT(980, "已超时"),
    /// 已中止
    ABORTED(990, "已中止");

    /// 状态码值
    private final int code;
    /// 状态标签描述
    private final String label;

    /// 状态码到枚举的不可变映射表，初始化时自动校验重复状态码
    private static final Map<Integer, Status> CODE_MAP = Arrays.stream(Status.values())
            .collect(Collectors.toUnmodifiableMap(Status::getCode, Function.identity()));

    /// 根据状态码获取枚举实例
    ///
    /// @param code 状态码
    /// @return 包含枚举实例的Optional，如果状态码无效则返回空Optional
    public static Optional<Status> fromCode(Integer code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }

    /// 根据状态码获取枚举实例，如果不存在则抛出异常
    ///
    /// @param code 状态码
    /// @return 对应的状态枚举实例
    /// @throws IllegalArgumentException 当状态码无效时抛出
    public static Status valueOf(Integer code) {
        return fromCode(code).orElseThrow(() -> new IllegalArgumentException("无效的状态码: " + code));
    }

    /// 判断状态码是否有效
    ///
    /// @param code 状态码
    /// @return 如果状态码有效返回true，否则返回false
    public static boolean isValidCode(Integer code) {
        return code != null && CODE_MAP.containsKey(code);
    }

    /// 返回状态的字符串表示
    ///
    /// 格式为：状态名称(状态码)，例如：ACTIVE(200)
    ///
    /// @return 状态的字符串表示
    @Override
    public String toString() {
        return this.name() + "(" + this.code + ")";
    }
}
