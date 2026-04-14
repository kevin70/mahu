---
name: mahu-lombok
description: 在 Mahu 仓库中处理 Lombok 选型、`record` 取舍、模型注解组合、构造器注入写法、`@Value` / `@Builder` / `@Getter` / `@Setter` / `@Data` / `@UtilityClass` 选择时必须使用。用户只要提到 Lombok、模型定义、DTO/command/query/result/entity 怎么写、是否该用 `record`、是否该补构造器或 builder，即使没有点名 skill，也要主动使用本 skill。
---

# Mahu Lombok

用于在 Mahu 仓库中统一 Lombok 相关判断。目标是减少“为了统一风格而统一”的低价值改动，并让模型写法与仓库现状保持一致。

## 先核对的事实来源

1. 根 `AGENTS.md`
2. `docs/content/2.architecture/4.module-boundaries.md`
3. 目标模块现有代码
4. 与 EBean 相关时，再核对 `mahu-entity`、`mahu-dal`、`mahu-task`、`mahu-admin:mahu-admin-infra`

如果规则与代码现状冲突，以更接近执行事实的代码和配置为准；如果需要偏离默认规范，要显式说明原因。

## 仓库内默认规则

- 对外可见、跨类可见、跨模块共享的模型，默认 **Lombok 优先于 `record`**。
- 只有类内部 `private` / `private static` 辅助类型，才可以不遵循上面的规则。
- 只使用贴合语义的最小 Lombok 注解集合，不要叠加语义重复的注解。
- 不要为了统一风格补 Lombok；已有显式业务构造器时，优先尊重现状。

## 模型选型

### 不可变模型

以下类型默认优先 `@Value`，需要构建器时加 `@Builder`：

- `query`
- 只读 `result`
- 跨模块共享的不可变模型
- 值对象

如果存在默认值，再考虑 `@Builder.Default`。

### 可变模型

以下类型默认优先 `@Getter/@Setter`：

- `command`
- 表单对象
- 请求/响应 bean

只有确认是简单可变数据载体时，才使用 `@Data`。

### 依赖注入类

以下类型统一使用 `final` 字段 + 构造器注入：

- `Service`
- `Controller`
- `Handler`
- `Interceptor`

新增代码优先 `@RequiredArgsConstructor`，`@AllArgsConstructor` 次之。

### 实体与工具类

- EBean Entity 继续使用 `@Getter/@Setter`
- 纯工具类优先 `@UtilityClass`

## 附加约束

- 敏感字段类型谨慎使用 `@Data` 或自动 `toString`
- 生成代码不强制套用本规范
- 当前 `mahu-model.query` 与 `mahu-shared` 只读共享模型，以 `@Value + @Builder` 为默认参考
- 不要为了一次转发、简单映射或未来假设场景引入新的包装模型

## 输出要求

回答或改动建议时，优先给出：

### 已核对事实

- 目标类型属于哪一类模型
- 参考了哪些现有类

### 建议写法

- 推荐的 Lombok 注解组合
- 是否应该避免 `record`

### 原因

- 这套写法如何贴合当前仓库规则和现有代码

## 示例

### 用户问模型该怎么写

用户：`这个 query 对象我想改成 record，可以吗？`

期望行为：

- 先判断它是不是对外可见、跨模块共享的模型
- 如果是，优先建议继续用 `@Value`，需要构建器时加 `@Builder`
- 说明这是仓库的默认规则，而不是通用 Java 偏好

### 用户问 command 怎么写

用户：`新增一个上传命令对象，用 @Data 还是 Getter/Setter？`

期望行为：

- 默认先建议 `@Getter/@Setter`
- 只有在明确是简单可变数据载体时才建议 `@Data`
- 如果对象带敏感字段，显式提醒自动 `toString` 风险
