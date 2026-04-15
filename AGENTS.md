# Mahu 项目 AGENTS 指南

面向在本仓库工作的 AI Agent：统一事实来源、查阅顺序、Agent 行为约束与权威入口，减少与项目现状不一致的回答或改动。

---

## 一、项目事实与查阅顺序

- **仓库名**：`rootProject.name = "mahu"`（`settings.gradle`）。
- **环境简称**：DEV / SIT / UAT / STG / PROD；代码定义见 `mahu-base/src/main/java/cool/houge/mahu/Env.java`，术语说明见 `docs/content/2.architecture/1.metadata-dictionary.md`。
- **本地依赖**：PostgreSQL、MinIO 等本地启动示例见 `docs/content/1.getting-started/2.installation.md`。
- **OpenAPI 前端工具链**：Node / `pnpm` 版本与 Gradle 集成以根 `build.gradle` 的 `node {}` 为准。
- **部署事实来源**：远程主机与 SSH 配置看根 `build.gradle` 的 `remotes`；镜像与部署 Task 看 `mahu-admin/mahu-admin-web/build.gradle`。
- **查阅优先级**：
  1. `docs/content/`、相关模块代码、`mahu-db/src/main/resources/changelog-root.yaml`
  2. 模块 `build.gradle`、根 `build.gradle`、`settings.gradle`
  3. 根 `README.md`（仅入口级信息）
  4. 再总结；缺信息时的推断必须明确标注为假设

---

## 二、模块概览

| 模块 | 说明 |
|------|------|
| `mahu-bom` | Java Platform / BOM；与 `gradle/libs.versions.toml` 一起约束版本 |
| `mahu-base` | 基础设施与通用代码 |
| `mahu-config` | 配置模型、配置前缀、配置相关枚举与 Blueprint |
| `mahu-entity` | EBean 实体与实体测试基线 |
| `mahu-model` | 非持久化业务模型；当前主要承载 `query` 查询对象 |
| `mahu-codegen` | 代码生成相关 |
| `mahu-db` | Liquibase；入口 `src/main/resources/changelog-root.yaml`，通过 `migrations/` 与 `data/` 管理结构和初始化数据 |
| `mahu-dal` | 数据访问层（Repository 等） |
| `mahu-shared` | 跨应用共享的领域/应用服务 |
| `mahu-web` | 工具型 Web 能力（Helidon WebServer 相关封装/基础能力） |
| `mahu-admin:mahu-admin-infra` | 管理端基础设施 |
| `mahu-admin:mahu-admin-web` | 管理端 Helidon SE Web/API 应用；OpenAPI Generator 输出到 `src/main/gen` |
| `mahu-task` | 独立定时任务服务（db-scheduler） |
| `docs`、`openapi` | 设计文档与规范资产（如 `openapi/dist/mahu-admin-openapi.yaml`） |

---

## 三、工作方式

- **语言**：默认使用中文输出与生成内容，包括说明、注释、提交信息、评审意见；仅在用户明确要求或上下文必须时使用英文。
- **表达**：简洁直接；说明中优先给出模块名、路径和关键约束；引用代码时使用 `起始行:结束行:路径`。
- **信源**：代码与配置优先于文档，文档优先于 `README.md`，通识最后使用。
- **工程规范入口**：代码约定、测试约定、最小实现与兼容性要求统一见 `docs/content/2.architecture/14.engineering-conventions.md`。

---

## 四、常用命令

根目录执行；引用 Task 前先在对应 `build.gradle` 或文档中核对，**不要臆造 Task 名**。

### 编译与测试

- `./gradlew build`：完整构建
- `./gradlew test`：全量测试
- `./gradlew jacocoRootReport`：聚合 JaCoCo 报告，输出到 `build/reports/jacoco/root`
- `./gradlew :mahu-entity:test`：单模块测试示例
- `./gradlew spotlessCheck`：格式检查
- `./gradlew spotlessApply`：格式化代码

### 代码生成

- `./gradlew openapiDepsInstall`：安装 `openapi/` 前端依赖
- `./gradlew :mahu-admin:mahu-admin-web:redoclyBundleOpenApi`：打包管理端 OpenAPI 规范
- `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate`：生成管理端 OpenAPI 代码，写入 `src/main/gen`

### 文档

- `cd docs && pnpm build`：构建文档站点
- `cd docs && pnpm dev`：本地预览文档站点

### 高风险入口

- `./gradlew :mahu-admin:mahu-admin-web:jib`：构建并推送管理端镜像
- `./gradlew :mahu-admin:mahu-admin-web:deployDev`：执行管理端开发环境部署
- 涉及 `docker`、`ssh`、远程部署、镜像推送等高风险操作时，必须先说明用途、影响范围、前置条件与回滚面，再由用户决定是否执行。
- 部署相关请求优先使用 `.agents/skills/mahu-deployment/SKILL.md`；`AGENTS.md` 只保留入口与边界，不承载部署长流程。

---

## 五、Ebean ORM

- 本项目使用 [Ebean ORM](https://ebean.io)。
- 涉及 Ebean 相关任务时，常见 step-by-step 指南以官方 guides 为准，执行前先查阅对应 guide，再结合仓库现状落地实现。
- 通用 guides 入口：<https://github.com/ebean-orm/ebean/tree/HEAD/docs/guides/>
- 任务与 guide 对应关系：
  - 依赖接入或比对依赖配置：<https://raw.githubusercontent.com/ebean-orm/ebean/HEAD/docs/guides/add-ebean-postgres-maven-pom.md>
  - 数据库配置：<https://raw.githubusercontent.com/ebean-orm/ebean/HEAD/docs/guides/add-ebean-postgres-database-config.md>
  - 使用 query bean 编写查询：<https://raw.githubusercontent.com/ebean-orm/ebean/HEAD/docs/guides/writing-ebean-query-beans.md>
  - 持久化与事务：<https://raw.githubusercontent.com/ebean-orm/ebean/HEAD/docs/guides/persisting-and-transactions-with-ebean.md>
  - Testcontainers / PostgreSQL 测试容器：<https://raw.githubusercontent.com/ebean-orm/ebean/HEAD/docs/guides/add-ebean-postgres-test-container.md>
  - DB migration generation：<https://raw.githubusercontent.com/ebean-orm/ebean/HEAD/docs/guides/add-ebean-db-migration-generation.md>
  - Lombok 与实体 Bean：<https://raw.githubusercontent.com/ebean-orm/ebean/HEAD/docs/guides/lombok-with-ebean-entity-beans.md>
- 与仓库现状对齐时，优先核对 `mahu-entity`、`mahu-dal`、`mahu-task`、`mahu-admin:mahu-admin-infra` 中已有 Ebean 用法，再决定是否调整；不要机械照搬 guide 示例。

---

## 六、Skills 与权威来源

- 仓库内 Skills 位于 `.agents/skills/*/SKILL.md`。
- 与上游对齐与校验见根目录 `skills-lock.json`（`computedHash`）；仓库本地自定义 skill 不强制登记在该文件内。
- 高风险专项流程优先沉淀为 skill；`AGENTS.md` 只保留入口、边界和事实来源，不承载长流程手册。
- 用户提到 Lombok、`record`、模型注解组合、`@Value` / `@Builder` / `@Data` 取舍时，优先使用 `.agents/skills/mahu-lombok/SKILL.md`。
- 用户提到 Helidon、Service Registry、`@Service.Singleton`、`@Service.RunLevel`、Registry 测试装配时，优先使用 `.agents/skills/mahu-helidon-service-registry/SKILL.md`。
- 用户提到部署、发布、镜像、`jib`、`deployDev`、远程主机、上线检查时，优先使用 `.agents/skills/mahu-deployment/SKILL.md`。
- Skills、模块 `README`、`build.gradle`、`docs/` 冲突时，以更接近代码执行事实的内容为准；无法确认时优先代码与配置。
