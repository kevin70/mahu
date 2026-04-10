# Mahu

Mahu 是一个基于 Java 25、Helidon SE、EBean 与 PostgreSQL 的 Gradle 多模块后端仓库，核心运行应用包括管理端 API 服务与独立任务服务。

详细架构、模块职责、数据库规范与 OpenAPI 工作流请优先阅读 `docs/` 文档站点。启动文档站后，可从首页进入对应章节。

## 文档入口

1. 文档首页：`docs/content/index.md`
2. 项目概览：`/getting-started/introduction`，源文件 `docs/content/1.getting-started/1.introduction.md`
3. 环境搭建与启动：`/getting-started/installation`，源文件 `docs/content/1.getting-started/2.installation.md`
4. 本地运行服务：`/getting-started/run-services`，源文件 `docs/content/1.getting-started/4.run-services.md`
5. 系统总览：`/architecture/system-overview`，源文件 `docs/content/2.architecture/0.system-overview.md`
6. 模块职责与边界：`/architecture/module-boundaries`，源文件 `docs/content/2.architecture/4.module-boundaries.md`
7. OpenAPI 工作流：`/architecture/openapi-workflow`，源文件 `docs/content/2.architecture/10.openapi-workflow.md`

## 运行应用

- `mahu-admin:mahu-admin-web`：管理端 API 服务
- `mahu-task`：独立任务与调度服务

## 相关项目

- [mahu-dashboard（后台仪表盘）](https://gitee.com/kk70/mahu-dashboard)

## 开发准备

- [Java 25](https://adoptium.net/zh-CN)
- [Docker](https://www.docker.com/)
- [Node.js / pnpm](https://pnpm.io/)（用于 `docs/` 与 `openapi/`）

IDE、格式化与包分层规范请直接看 `docs/` 中的详细说明，避免 README 与文档站重复维护。

## 快速开始

1. 启动本地依赖：PostgreSQL、MinIO
2. 在项目根目录执行 `./gradlew build`
3. 如需生成管理端接口代码，执行 `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate`
4. 如需启动文档站，执行 `cd docs && pnpm install && pnpm dev`
5. 如需启动服务，参考 `/getting-started/run-services`

## 常用命令

- 构建：`./gradlew build`
- 测试：`./gradlew test`
- 单模块测试示例：`./gradlew :mahu-web:test`
- 格式化自检：`./gradlew spotlessCheck`
- 应用格式化：`./gradlew spotlessApply`
- 生成管理端 OpenAPI 服务器端代码：`./gradlew :mahu-admin:mahu-admin-web:openApiGenerate`
- 构建文档站点：`cd docs && pnpm build`
- 本地预览文档站点：`cd docs && pnpm install && pnpm dev`

## 说明

- README 只保留仓库入口级信息。
- 详细环境搭建、数据库规范、模块边界、配置体系与 OpenAPI 说明统一维护在 `docs/`。
