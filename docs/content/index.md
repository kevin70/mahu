---
title: Mahu 开发文档
description: 面向 Mahu 内部开发者的项目总览、架构规范、运行指南与数据库/接口约定入口。
seo:
  title: Mahu 开发文档
  titleTemplate: '%s'
  description: 面向 Mahu 内部开发者的项目总览、架构规范、运行指南与数据库/接口约定入口。
---

::u-page-hero
---
class: dark:bg-gradient-to-b from-neutral-900 to-neutral-950
---
#title
Mahu 开发文档

#description
面向 Mahu 内部开发者的项目总览、架构规范、运行指南与数据库/接口约定入口。

#links
  :::u-button
  ---
  color: neutral
  size: xl
  to: /getting-started/introduction
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
  icon: i-lucide-workflow
  to: /architecture/system-overview
  ---
  #title
  系统总览

  #description
  从模块关系、运行应用与支撑资产快速理解整个仓库。
  :::

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
  icon: i-lucide-git-branch-plus
  to: /architecture/request-lifecycle
  ---
  #title
  典型请求链路

  #description
  从 OpenAPI 到 Controller、Service、Repository 的真实落地路径。
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
  icon: i-lucide-play
  to: /getting-started/run-services
  ---
  #title
  本地运行服务

  #description
  启动管理端与任务服务，并快速验证环境是否可用。
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
