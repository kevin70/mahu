---
seo:
  title: Mahu 开发文档
  description: 面向 Mahu 内部开发者的项目总览、架构设计与数据库规范入口。
---

::u-page-hero
---
class: dark:bg-gradient-to-b from-neutral-900 to-neutral-950
---
#title
Mahu 开发文档

#description
面向 Mahu 内部开发者的项目总览、架构规范与数据库/接口约定入口。

#links
  :::u-button
  ---
  color: neutral
  size: xl
  to: /getting-started/installation
  trailing-icon: i-lucide-arrow-right
  ---
  快速开始
  :::

  :::u-button
  ---
  color: neutral
  icon: i-lucide-eye
  size: xl
  to: /architecture/observability
  variant: outline
  ---
  进入可观测性入口
  :::
::

::u-page-section
#title
关键规范与架构入口

#features
  :::u-page-feature
  ---
  icon: i-lucide-eye
  to: /architecture/observability
  ---
  #title
  可观测性与错误响应

  #description
  统一错误响应结构与 `trace_id` 约定。
  :::

  :::u-page-feature
  ---
  icon: i-lucide-shield-check
  to: /architecture/testing-and-quality-gates
  ---
  #title
  测试与质量门槛

  #description
  覆盖率门槛、OpenAPI 回归与常用命令。
  :::

  :::u-page-feature
  ---
  icon: i-lucide-layers
  to: /architecture/module-boundaries
  ---
  #title
  模块职责与边界

  #description
  服务分层与 Helidon 约定的职责边界。
  :::

  :::u-page-feature
  ---
  icon: i-lucide-database
  to: /architecture/database-entity-consistency
  ---
  #title
  数据库与实体一致性

  #description
  Liquibase、实体字段与 repository 查询条件一致性。
  :::

  :::u-page-feature
  ---
  icon: i-lucide-cpu
  to: /ai/mcp
  ---
  #title
  MCP 接入 Mahu 文档

  #description
  将 Mahu 文档接入 Claude、Cursor、VS Code 等 AI 工具。
  :::
::
