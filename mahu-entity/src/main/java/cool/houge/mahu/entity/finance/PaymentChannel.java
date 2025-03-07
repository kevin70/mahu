package cool.houge.mahu.entity.finance;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/// 支付渠道
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "finance", name = "payment_channel")
public class PaymentChannel {

    /// 主键
    @Id
    private String id;
    /// 创建时间
    @WhenCreated
    private LocalDateTime createdAt;
    /// 更新时间
    @WhenModified
    private LocalDateTime updatedAt;
    /// 支付渠道名称（如：支付宝、微信支付、银行卡）
    private String name;
    /// 渠道状态
    ///
    /// - `T`：启用
    /// - `F`：禁用
    private boolean status;

    /// 微信应用 ID
    private String wechatAppid;
    /// 微信直连商户号
    private String wechatMchid;
    /// 商户 API 证书序列号 serial_no
    private String wechatSerialNo;
    /// 微信私钥
    private String wechatPrivateKey;
    /// 解密微信 APIv3 支付回调数据密钥
    private String wechatSecretKey;
    /// 微信支付回调地址
    private String wechatCallbackUrl;
}
