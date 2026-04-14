# Metrics

## 官方入口

- `Metrics`: `https://helidon.io/docs/latest/apidocs/io.helidon.metrics.api/io/helidon/metrics/api/Metrics.html`
- `Counter`: `https://helidon.io/docs/latest/apidocs/io.helidon.metrics.api/io/helidon/metrics/api/Counter.html`
- `Timer`: `https://helidon.io/docs/latest/apidocs/io.helidon.metrics.api/io/helidon/metrics/api/Timer.html`
- `MetricsObserver`: `https://helidon.io/docs/latest/apidocs/io.helidon.webserver.observe.metrics/io/helidon/webserver/observe/metrics/MetricsObserver.html`

## 推荐模式

- 先定义问题，再选指标：
  - 次数、成功/失败：`Counter`
  - 延迟：`Timer`
  - 大小或分布：`DistributionSummary`
  - 外部维护的瞬时状态：`Gauge`
- 通过 `Metrics.getOrCreate(...)` 获取全局 registry 中的 meter，避免重复注册。
- 指标命名保持稳定、可读、可聚合。
- 标签只保留低基数维度，例如：
  - `result=success|failure`
  - `operation=create|update`
  - `client=internal|external`

## 埋点位置

- HTTP 请求总量与延迟
- 外部调用成功率与延迟
- 定时任务执行次数、失败次数、执行时长
- 明确的业务动作，例如“发券成功”“缓存刷新失败”

## 不推荐写法

- 把用户 ID、订单号、traceId、完整 URL、带主键的 path 放进标签
- 每个异常类都打一个新指标
- 指标名频繁变化
- 用 metrics 代替审计日志

## 短例子

```java
class RemoteProfileClient {

    private final Counter failures = Metrics.getOrCreate(Counter.builder("remote.profile.failures"));
    private final Timer latency = Metrics.getOrCreate(Timer.builder("remote.profile.latency"));

    Profile loadProfile(String id) {
        try {
            return latency.record(() -> doLoad(id));
        } catch (RuntimeException e) {
            failures.increment();
            throw e;
        }
    }
}
```

## Observe 配合

- 暴露指标端点时优先通过 `MetricsObserver.create()` 接入 `ObserveFeature`
- 默认 `/metrics` 下会提供 `application`、`vendor`、`base` 子路径

## Fault Tolerance 相关提醒

`FaultTolerance` 文档明确说明其 metrics 默认关闭。需要 fault-tolerance 指标时，要显式启用，不要假设自动打开。

## 验证点

- 指标是否回答了明确问题
- 标签是否低基数
- 同一个指标名是否会长期稳定
- 是否通过 Observe 正确暴露指标端点
