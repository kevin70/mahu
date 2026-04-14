---
name: mahu-helidon-service-registry
description: 在 Mahu 仓库中处理 Helidon Service Registry、编译时 DI、`io.helidon.service.registry.Service` 注解、作用域选择、`@Service.PostConstruct` / `@Service.PreDestroy` / `@Service.RunLevel`、Service Registry 测试装配时必须使用。用户只要提到 Helidon DI、服务注册、生命周期、启动顺序、如何写 Service/Provider/Handler、如何测试依赖 Service Registry 的模块，即使没有点名 skill，也要主动使用本 skill。
---

# Mahu Helidon Service Registry

用于在 Mahu 仓库中统一 Helidon Service Registry 相关判断。目标是让依赖注入、生命周期和测试方式与现有模块保持一致，而不是退回到手工装配。

## 先核对的事实来源

1. 根 `AGENTS.md`
2. `docs/content/2.architecture/4.module-boundaries.md`
3. `docs/content/2.architecture/7.testing-and-quality-gates.md`
4. 目标模块现有代码与 `build.gradle`

如果文档与代码冲突，以代码与配置为准。

## 仓库内默认规则

- 本仓库使用 Helidon Service Registry（编译时 DI），通过 `io.helidon.service.registry.Service` 注解完成依赖注入。
- 已依赖 `helidon-service-registry` 的模块，默认继续使用这套模式，不回退到手工装配。
- 注入方式优先构造器注入，字段保持 `final`；可以配合 Lombok 构造器使用。

## 注解选型

### 作用域

按职责选择：

- `@Service.Singleton`
- `@Service.PerLookup`
- `@Service.PerRequest`

如果没有明确理由，优先与目标模块现有同类组件保持一致。

### 生命周期

- 初始化逻辑放在 `@Service.PostConstruct`
- 清理逻辑放在 `@Service.PreDestroy`

不要把资源初始化和释放塞进调用路径里，导致生命周期分散。

### 启动顺序

- 启动顺序敏感时使用 `@Service.RunLevel`
- 只有在确实存在先后依赖时才加；不要无理由堆叠启动顺序注解

## 测试配套

- 对已依赖 `helidon-service-registry` 的模块，测试默认优先 Service Registry 模式。
- `mahu-shared`、`mahu-task` 的常见基线是 `@Testing.Test(perMethod = true)` + `Services.set(...)`。
- 一旦测试类已经使用 Helidon Testing / Service Registry 注解，就延续该模式，不退回手工 `new` 复杂依赖。
- `mahu-web` 这类轻量模块仍以纯 JUnit + Mockito 为主，除非目标能力明确依赖 Service Registry。

## 使用边界

- 不要为了“一致”把原本不依赖 Service Registry 的简单工具类强行改成服务。
- 不要新增只有一层转发的 Provider / Service 包装。
- 不要把模块边界问题伪装成 DI 问题；先判断职责应落在哪个模块、哪一层。

## 输出要求

回答或改动建议时，优先给出：

### 已核对事实

- 目标模块是否已依赖 `helidon-service-registry`
- 目标类在现有代码中最接近哪类组件

### 建议写法

- 推荐的作用域
- 是否需要生命周期注解
- 是否需要 `RunLevel`
- 测试应沿用哪种装配方式

### 原因

- 为什么这套写法符合仓库现状，而不是泛化的 Helidon 最佳实践

## 示例

### 用户问服务怎么注册

用户：`这个缓存服务该用什么注解注册？`

期望行为：

- 先看目标模块和同类服务现状
- 默认建议合适的 `@Service.*` 作用域
- 如果存在启动阶段预热，再判断是否需要 `@Service.RunLevel` 和 `@Service.PostConstruct`

### 用户问测试怎么写

用户：`这个 shared service 的测试要不要直接 new？`

期望行为：

- 先指出 `mahu-shared` 默认优先 Service Registry 测试模式
- 建议 `@Testing.Test(perMethod = true)` + `Services.set(...)`
- 只在依赖极轻且不涉及 Registry 语义时，才考虑更轻的装配方式
