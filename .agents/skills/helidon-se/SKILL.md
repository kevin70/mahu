---
name: helidon-se
description: 在 Helidon SE 4.x 项目中给出当前推荐写法与实现决策。用户只要提到 Helidon SE、WebServer、`HttpRouting`、`HttpFeature`、`Config`、`Observe`、`WebClient`、Service Registry、事件、定时任务、metrics、fault-tolerance、Helidon testing、最佳实践或“推荐写法”，即使没有点名 skill，也要主动使用本 skill。优先给出当前官方 API 和更现代的实现方式；如果目标项目已有更强约束，再与项目现状对齐。
---

# Helidon SE

用于在当前 Helidon SE 4.x 语境下回答“现在该怎么写”。目标不是讲升级历史，而是优先给出当前可维护、可测试、贴近官方 API 的方案。

## 先核对的事实

1. 目标项目锁定的 Helidon BOM 版本。
2. 实际依赖了哪些模块：`webserver`、`config`、`service-registry`、`webclient`、`observe`、`metrics`、`fault-tolerance`、`scheduling`、`testing`。
3. 目标项目是否已有更强约束，例如模块边界、命名约定、统一的 Provider / Feature / Controller 组织方式。
4. 如果问题涉及 metrics 或 testing，还要核对目标模块的工程约定、现有测试基线和实际依赖，不要只看官方 API。

如果项目规则和官方推荐冲突，先说明冲突，再决定是延续现状还是建议重构。

## 默认决策

- 优先当前 Helidon SE 4.x API，不主动展开 3.x 迁移或旧版兼容。
- 回答前先对齐目标项目约束；官方当前推荐和仓库既有基线冲突时，必须先显式说明冲突，再给出“延续现状”或“建议调整”的判断。
- 依赖注入优先 Service Registry 的编译时装配；生产代码优先构造器注入，避免在业务路径里频繁使用 `Services.get(...)`。
- 需要聚合路由、过滤器和异常处理时，优先实现 `HttpFeature`；只暴露单一资源或单一路由簇时，用 `HttpService`。
- 需要显式控制资源生命周期的客户端、数据库、观察能力等，优先 `@Service.Singleton` + `Supplier<T>` + `@Service.PreDestroy`。
- 需要启动初始化或后台任务注册时，优先 `@Service.PostConstruct`；只有启动顺序确实敏感时才加 `@Service.RunLevel`。
- 事件只用于副作用或解耦的通知链路，不用于主业务必经分支。
- 定时任务优先“注册逻辑”和“执行逻辑”分离，保留 `executeAt(Instant)` 这类可测试入口。
- metrics 只记录低基数、可聚合、能回答明确问题的指标。
- testing 不能按 Helidon 能力一刀切，必须区分目标模块：
  - `mahu-web` 优先纯 JUnit + Mockito 轻量单测。
  - `mahu-admin:mahu-admin-web` 或确实要验证路由/过滤器/序列化边界的 WebServer 场景，才优先 `helidon-webserver-testing-junit5`。
  - `mahu-shared`、`mahu-task` 等依赖 Service Registry 的模块，优先 `@Testing.Test(perMethod = true)` + `Services.set(...)`。
- fault-tolerance 只包裹真实不稳定的边界，不把重试/超时/熔断铺满普通本地逻辑。

## 何时读哪个 reference

- WebServer、路由、Filter、`HttpFeature`：读 [references/webserver.md](references/webserver.md)
- `Config`、配置树、typed config：读 [references/config.md](references/config.md)
- Service Registry、作用域、Provider、生命周期：读 [references/service-registry.md](references/service-registry.md)
- `WebClient`、`ObserveFeature`：读 [references/webclient-observe.md](references/webclient-observe.md)
- 事件：读 [references/events.md](references/events.md)
- 定时任务、调度：读 [references/scheduled-tasks.md](references/scheduled-tasks.md)
- metrics：读 [references/metrics.md](references/metrics.md)
- fault-tolerance：读 [references/fault-tolerance.md](references/fault-tolerance.md)
- 测试：读 [references/testing.md](references/testing.md)

## 输出要求

回答或改动建议时，按这个顺序组织：

1. 已核对事实
2. 推荐写法
3. 为什么这是当前更优方案
4. 如果有项目约束或推断，显式标出来，并说明是否优先服从仓库现状

## 官方依据

- `Config`：`https://helidon.io/docs/latest/apidocs/io.helidon.config/io/helidon/config/Config.html`
- `WebServer` / `HttpFeature`：`https://helidon.io/docs/latest/apidocs/io.helidon.webserver/`
- `Service Registry` / `Services`：`https://helidon.io/docs/latest/apidocs/io.helidon.service.registry/`
- `ObserveFeature` / `MetricsObserver`：`https://helidon.io/docs/latest/apidocs/io.helidon.webserver.observe/`
- `WebClient` / `JacksonSupport`：`https://helidon.io/docs/latest/apidocs/io.helidon.webclient.api/`
- `Scheduling`：`https://helidon.io/docs/latest/apidocs/io.helidon.scheduling/`
- `MeterRegistry` / `Metrics`：`https://helidon.io/docs/latest/apidocs/io.helidon.metrics.api/`
- `FaultTolerance`：`https://helidon.io/docs/latest/apidocs/io.helidon.faulttolerance/`

如果官方 API 和目标项目现状不一致，以“能在目标项目稳定落地”为准，但要把偏离点讲清楚。
