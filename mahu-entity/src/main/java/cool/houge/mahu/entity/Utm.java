package cool.houge.mahu.entity;

import lombok.Data;

/// UTM 是 Urchin Tracking Module 的缩写（来源于早期的 Urchin Analytics，后来被 Google 收购并演化为 Google Analytics）。
/// 在现代网络/营销中，UTM 指一组放在 URL 上的查询参数，用来标识流量的来源和营销活动，便于流量与转化的归因分析。
///
/// 为什么要用 UTM？
///
/// - 精确归因：把访问/注册/下单等事件归因到具体的广告、邮件或渠道。
/// - 统一分析：与 Google Analytics 或自建分析系统结合，按 campaign/source/medium 汇总效果。
/// - 支持 A/B 与多渠道比对：通过 utm_content / utm_campaign 区分不同创意或测试组。
///
/// @author ZY (kzou227@qq.com)
@Data
public class Utm {

    /// 流量来源。例如 google、newsletter、wechat。用于识别流量来自哪个平台/渠道。
    private String utmSource;
    /// 媒介/类型，例如 cpc、email、social、referral、affiliate。
    private String utmMedium;
    /// 活动名称，用于区分不同营销活动或推广系列，例如 spring_sale_2026。
    private String utmCampaign;
    /// 关键字，常用于付费搜索来记录关键词。
    private String utmTerm;
    /// 内容区分，在同一广告/链接有多个变体时用于区分（例如 button_a vs button_b）。
    private String utmContent;
}
