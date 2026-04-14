# Events

## 官方入口

- `io.helidon.service.registry` 包概览：`https://helidon.io/docs/latest/apidocs/io.helidon.service.registry/io/helidon/service/registry/package-summary.html`

## 推荐模式

- 事件对象保持强类型、语义单一、字段最小化。
- 发事件的一方只负责发布，不知道谁在消费。
- `@Observer`
  - 用于必须跟主流程一起完成、时序敏感、可能共享事务的副作用
- `@AsyncObserver`
  - 用于审计、通知、异步落库、次要链路
  - 处理器内部自己兜底异常和日志，不把失败静默吞掉
- 事件处理器保持幂等或至少可重入，尤其是异步副作用链路

## 适用场景

- 审计日志
- 通知
- 统计与留痕
- 次要链路解耦

## 不推荐写法

- 用事件代替主业务控制流
- 一个事件再触发多个层层嵌套的事件链，导致时序不透明
- 在事件里塞整块聚合根或大量请求上下文
- 异步观察者不记录失败，出了问题无法排查

## 短例子

```java
@Service.Singleton
class TokenService {
    private final Event.Emitter<AdminLoginLogEvent> emitter;

    void onLoginSuccess(AdminLoginLogEvent event) {
        emitter.emit(event);
    }
}

@Service.Singleton
class AdminLoginLogEventHandler {

    @AsyncObserver
    void onAdminLoginLogEvent(AdminLoginLogEvent event) {
        // 审计副作用，自己兜底异常并记录日志
    }
}
```

## 验证点

- 事件是否只承载一个明确语义
- 同步 / 异步观察者选择是否有原因
- 异步处理失败是否留痕
- 主流程是否仍然清晰，不依赖隐藏事件链
