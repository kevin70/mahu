# Fault Tolerance

## 官方入口

- `FaultTolerance`: `https://helidon.io/docs/latest/apidocs/io.helidon.faulttolerance/io/helidon/faulttolerance/FaultTolerance.html`
- `Retry`: `https://helidon.io/docs/latest/apidocs/io.helidon.faulttolerance/io/helidon/faulttolerance/Retry.html`
- `Timeout`: `https://helidon.io/docs/latest/apidocs/io.helidon.faulttolerance/io/helidon/faulttolerance/Timeout.html`
- `CircuitBreaker`: `https://helidon.io/docs/latest/apidocs/io.helidon.faulttolerance/io/helidon/faulttolerance/CircuitBreaker.html`
- `Fallback`: `https://helidon.io/docs/latest/apidocs/io.helidon.faulttolerance/io/helidon/faulttolerance/Fallback.html`

## 当前要点

- 所有 handler 都是阻塞式的；官方 package summary 明确建议它们运行在 virtual threads 上。
- `FaultTolerance.config(Config)` 和 `FaultTolerance.executor(...)` 已在 4.3 起标记为待移除，不要在新代码里继续依赖。
- fault-tolerance metrics 默认关闭，需要显式启用。

## 推荐模式

- 只包裹真实不稳定边界，例如外部 HTTP、缓存、消息、第三方 API。
- 直接在边界处组装 handler 链，不要把 fault-tolerance 注入到领域对象的核心逻辑里。
- 选择策略时按失败模式来：
  - `Timeout`: 单次调用必须有上限
  - `Retry`: 确认是瞬时失败且操作可重试
  - `CircuitBreaker`: 连续失败会放大雪崩时
  - `Bulkhead`: 需要限制并发和排队时
  - `Fallback`: 其他策略用尽后的最后退路
- 通常把 `Fallback` 放在最外层，把它作为最后兜底，而不是第一层。

## 不推荐写法

- 对本地纯计算逻辑套 fault-tolerance
- 对非幂等写操作默认加重试
- 不区分异常类型，所有异常都重试
- 超时、重试、熔断全开，但没有任何日志、指标和降级语义
- 继续使用已标记废弃的全局 `FaultTolerance.config(...)`

## 短例子

```java
class RemoteProfileGateway {

    private final FtHandlerTyped<Profile> guarded = FaultTolerance.<Profile>typedBuilder()
            .addTimeout(Timeout.create(Duration.ofSeconds(2)))
            .addRetry(Retry.create(retry -> {
                // 有限次数，只覆盖瞬时失败
            }))
            .addBreaker(CircuitBreaker.create(breaker -> {
                // 失败阈值与恢复窗口
            }))
            .addFallback(Fallback.createFromMethod(ex -> cachedProfile()))
            .build();

    Profile loadProfile() {
        return guarded.invoke(this::callRemote);
    }
}
```

## 选择原则

- `Retry`
  - 只给幂等调用
  - 只重试瞬时异常
  - 次数要小，避免把小故障放大成雪崩
- `Timeout`
  - 每次调用都要有明确上限
  - 超时值来自 SLA，不来自拍脑袋
- `CircuitBreaker`
  - 用在失败会持续堆积的外部依赖
  - 不是替代监控的工具
- `Bulkhead`
  - 用于昂贵资源或共享下游
  - 并发和排队限制都要有理由
- `Fallback`
  - 返回陈旧值、空结果、降级视图时才有意义
  - 不要把真实错误吞成“看起来成功”

## 验证点

- fault-tolerance 是否只出现在不稳定边界
- 是否区分了幂等与非幂等调用
- 是否有超时、重试、熔断、降级的明确业务语义
- 是否补上了日志、metrics 或其他可观测性
