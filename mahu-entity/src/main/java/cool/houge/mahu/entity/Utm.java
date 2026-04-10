package cool.houge.mahu.entity;

import lombok.Getter;
import lombok.Setter;

/// UTM 追踪参数数据对象
///
/// UTM 是 Urchin Tracking Module 的缩写，用于标识 URL 上的流量来源和营销活动。
/// 支持五个标准参数：source（来源）、medium（媒介）、campaign（活动）、term（关键词）、content（内容）。
///
/// 为什么要用 UTM？
/// - 精确归因：把访问/注册/下单等事件归因到具体的广告、邮件或渠道。
/// - 统一分析：与 Google Analytics 或自建分析系统结合，按 campaign/source/medium 汇总效果。
/// - 支持 A/B 与多渠道比对：通过 utm_content / utm_campaign 区分不同创意或测试组。
///
/// 使用示例：
///
/// 1. **创建 UTM 对象**
/// ```java
/// Utm utm = new Utm();
/// utm.setUtmSource("google");        // 流量来自 Google
/// utm.setUtmMedium("cpc");           // 通过 CPC 付费搜索
/// utm.setUtmCampaign("shoes_launch_202603");  // 鞋类产品发布活动
/// utm.setUtmTerm("running shoes");   // 搜索关键词
/// utm.setUtmContent("ad_variant_a"); // 广告变体 A
/// ```
///
/// 2. **提取 URL 中的 UTM 参数**
/// ```java
/// // URL: https://example.com/landing?utm_source=wechat&utm_medium=social&utm_campaign=spring_sale
/// Utm utm = new Utm();
/// utm.setUtmSource("wechat");
/// utm.setUtmMedium("social");
/// utm.setUtmCampaign("spring_sale");
/// // 存储或分析
/// ```
///
/// 3. **与用户/订单关联存储**
/// ```java
/// // 用户注册时捕获 UTM 参数
/// User user = new User();
/// user.setUtmSource(utm.getUtmSource());
/// user.setUtmMedium(utm.getUtmMedium());
/// user.setUtmCampaign(utm.getUtmCampaign());
/// // 后续分析该用户的转化路径
/// ```
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
public class Utm {

    /// 流量来源
    /// 例如 google、newsletter、wechat，用于识别流量来自哪个平台/渠道。
    /// 建议值：google、baidu、wechat、qq、weibo、newsletter、partner 等
    private String utmSource;

    /// 媒介/类型
    /// 例如 cpc、email、social、referral、affiliate，用于区分推广方式。
    /// 建议值：organic、cpc、email、social、referral、affiliate、offline、sms 等
    private String utmMedium;

    /// 活动名称
    /// 用于区分不同营销活动或推广系列，例如 spring_sale_2026。
    /// 命名建议：{product}_{action}_{yyyymm}，如 shoes_launch_202601
    private String utmCampaign;

    /// 关键词（可选）
    /// 常用于付费搜索来记录用户的搜索关键词。
    /// 仅在付费搜索（CPC）场景下通常使用。
    private String utmTerm;

    /// 内容区分（可选）
    /// 在同一广告/链接有多个变体时用于区分，例如 button_a vs button_b。
    /// 用于 A/B 测试时标识不同的创意或位置。
    private String utmContent;
}
