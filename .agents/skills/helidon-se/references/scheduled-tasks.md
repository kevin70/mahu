# Scheduled Tasks

## 官方入口

- `Scheduling`: `https://helidon.io/docs/latest/apidocs/io.helidon.scheduling/io/helidon/scheduling/Scheduling.html`
- `FixedRate`: `https://helidon.io/docs/latest/apidocs/io.helidon.scheduling/io/helidon/scheduling/FixedRate.html`
- `Task`: `https://helidon.io/docs/latest/apidocs/io.helidon.scheduling/io/helidon/scheduling/Task.html`

## 推荐模式

- 新代码优先 `FixedRate.builder()` / `Cron.builder()`，不要再用已标记待移除的 `Scheduling.fixedRate()` / `Scheduling.cron()`。
- 对基础设施 worker、缓存刷新、扫描器、补偿任务，优先程序化注册：
  - `@Service.Singleton`
  - `@Service.PostConstruct` 里创建 task
  - 通过 `TaskManager.register(task)` 注册
- 保留显式执行入口，例如 `execute()` 和 `executeAt(Instant)`，方便测试和失败兜底。
- 把调度线程入口和真正业务逻辑分开，调度入口统一兜底异常，避免线程被未捕获异常打断。
- 调度参数优先从配置读取，代码里保留默认间隔和开关。

## 何时用注解式调度

- `@Scheduling.FixedRate`
- `@Scheduling.Cron`

适合非常简单、局部、自包含的方法。

如果任务依赖多个服务、需要配置覆盖、注册顺序、关闭控制、可测执行入口，优先程序化注册。

## 何时不用 Helidon Scheduling

- 需要持久化任务状态
- 需要集群协调
- 需要错过触发后的补偿执行
- 需要复杂租约、重试、恢复逻辑

这些场景应考虑专门的持久化调度器，而不是把 in-memory `FixedRate` / `Cron` 硬撑成分布式任务系统。

## 不推荐写法

- 把业务逻辑全部塞进 `.task(inv -> { ... })`
- 没有显式执行入口，导致测试只能等真实调度触发
- 调度线程异常直接冒出，导致任务悄悄停掉
- 用固定频率任务去模拟需要持久化保证的业务调度

## 短例子

```java
@Service.Singleton
class RefreshWorker {

    private final Config config;
    private final TaskManager taskManager;

    @Service.PostConstruct
    void init() {
        var task = FixedRate.builder()
                .interval(Duration.ofMinutes(5))
                .config(config.get("scheduling.refresh"))
                .task(inv -> execute())
                .build();
        taskManager.register(task);
    }

    void execute() {
        try {
            executeAt(Instant.now());
        } catch (Exception e) {
            log.error("scheduled task failed", e);
        }
    }

    void executeAt(Instant now) {
        // 真正业务逻辑
    }
}
```

## 验证点

- 是否使用了 `FixedRate.builder()` / `Cron.builder()`
- 是否分离了调度入口和业务执行入口
- 是否可以直接测试 `executeAt(...)`
- 是否区分了 in-memory 调度和持久化调度
