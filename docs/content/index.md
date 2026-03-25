---
title: Mahu 开发文档
description: 面向 Mahu 内部开发者的项目总览、架构设计与数据库规范入口。
seo:
  title: Mahu 开发文档
  description: 面向内部开发者的 Mahu 项目说明、架构设计与数据库规范。
---

::u-page-hero{class="dark:bg-gradient-to-b from-neutral-900 to-neutral-950"}
---
orientation: horizontal
---
#top
:hero-background

#title
[Mahu 开发文档]{.text-primary}

#description
面向 Mahu 内部开发者的项目总览、架构规范与数据库/接口约定入口。

## 快速入口

:::card-group
  :::card
  ---
  icon: i-lucide-eye
  title: 可观测性与错误响应
  to: /architecture/observability
  ---
  统一错误响应结构与 `trace_id` 约定。
  :::
  :::card
  ---
  icon: i-lucide-shield-check
  title: 测试与质量门槛
  to: /architecture/testing-and-quality-gates
  ---
  覆盖率门槛、OpenAPI 回归与常用命令。
  :::
  :::card
  ---
  icon: i-lucide-layers
  title: 模块职责与边界
  to: /architecture/module-boundaries
  ---
  服务分层与 Helidon 约定的职责边界。
  :::
  :::card
  ---
  icon: i-lucide-database
  title: 数据库与实体一致性
  to: /architecture/db-entity-consistency
  ---
  Liquibase、实体字段与 repository 查询条件一致性。
  :::
:::
