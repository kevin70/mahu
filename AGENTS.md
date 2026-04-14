# Mahu 项目 AGENTS 指南

面向在本仓库工作的 AI Agent：统一事实来源、模块边界、编码习惯与测试约定，减少与项目现状不一致的回答或改动。

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
- **模块边界**：改动必须遵守 `docs/content/2.architecture/4.module-boundaries.md`。
- **兼容性**：涉及库表或 API 不兼容变更时，必须写明影响范围与迁移建议。
- **不要臆造**：Gradle Task、环境名、部署入口、配置键、包结构都必须先核对仓库事实。
- **优先最小实现**：优先直接实现当前需求；新增抽象、包装、工厂方法、事件链、适配层或辅助类型前，必须能明确说明收益。
- **禁止低价值抽象**：若某层封装不能减少重复、降低耦合、提升可测试性或解决当前真实问题，就不要引入；不要为了统一风格、未来假设场景或“看起来更整齐”而增加层次。

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

## 五、代码约定

### 命名与结构

- **包与分层命名**：遵循 `docs/content/1.getting-started/1.introduction.md` 与 `docs/content/2.architecture/4.module-boundaries.md` 的 Java Package / 模块边界规范（`entity/repository/service/controller/shared/util`）。
- **后缀约定**：`Repository`、`Service`、`Controller` 后缀为必选约定。
- **Java 标识符**：以 `checkstyle.xml` 为准；包名全小写点分，成员/局部变量 camelCase，方法名 camelCase；测试方法允许下划线表达语义。
- **业务常量**：主题、状态、枚举值禁止硬编码字符串；生产代码与测试代码都应通过常量或枚举获取。
- **数据库迁移命名**：沿用 `VYYYYMMDD_NNN__description.sql`；`description` 使用小写下划线短语；文件按时间与序号递增。
- **配置键命名**：统一 `kebab-case`，按领域分组（如 `db.*`、`scheduling.*`），禁止同层级混用 `camelCase` / `snake_case`。

### 抽象与封装

- **最小抽象原则**：能用直接构造、内聚私有方法、现有类型完成的逻辑，不要额外抽成低价值工厂方法、包装方法或仅做转发的辅助类。
- **禁止空转发层**：不要新增只做委托调用的 Service、Repository 包装、Mapper 转发、异常工厂或事件转发。
- **重构目标**：重构优先删除复杂度、合并重复和收敛命名，不要在没有明确收益时制造新的抽象层。

### Lombok 与 Helidon 约束

- **Lombok**：选型、注解组合、`record` 取舍、模型分类、敏感字段约束统一遵循 `.agents/skills/mahu-lombok/SKILL.md`。
- **Helidon DI**：Service Registry 的作用域、生命周期、启动顺序、测试配套与注入方式统一遵循 `.agents/skills/mahu-helidon-service-registry/SKILL.md`。
- **保留原则**：两者都只保留仓库级稳定约束；遇到与代码现状冲突时，优先核对模块代码、`build.gradle` 与 `docs/content/2.architecture/4.module-boundaries.md`。

---

## 六、测试约定

### 通用规则

- 每个测试方法独立初始化测试上下文，不共享可变状态。
- 被测对象优先通过项目既有 DI / 测试基线注入，避免手工 `new` 复杂依赖。
- Mock 优先使用 Mockito；mock 字段建议 `private final`，在每个用例开始前完成装配。
- 测试方法命名统一 `行为_结果`，直接表达业务语义。
- 每组用例至少覆盖一条成功路径和一条失败或边界路径。
- 时间、排序、ID 等易波动断言使用稳定输入，如 `Instant.parse(...)`、显式排序断言、负数业务 ID。
- 查询类至少验证“过滤条件生效 + 排序规则生效”；更新类至少验证“返回影响行数 + 落库状态一致”。
- 每个新增测试都应能回答“验证了哪条业务规则”。

### 按模块选型

- `mahu-dal`：优先真实数据库集成测试，基线为 `PostgresLiquibaseTestBase`；使用 Testcontainers PostgreSQL + Liquibase（`sit` 标签），每个用例开启事务并默认回滚。
- `mahu-dal`：数据构造以 Instancio `Model` 为主，忽略 `id/createdAt/updatedAt/version` 等波动字段。
- `mahu-shared`：优先 `@Testing.Test(perMethod = true)` + `Services.set(...)`，通过 Service Registry 注入被测对象。
- `mahu-task`：与 `mahu-shared` 一致，优先测试 `executeAt(Instant)` 这类可控入口，避免调度线程副作用。
- `mahu-web`：优先纯 JUnit + Mockito 轻量单测，重点验证异常匹配、错误响应映射与回退逻辑。
- `mahu-entity`：当前提供测试环境 DI 组件，用于为依赖模块提供测试期 DataSource / Database 覆盖。
- `mahu-admin:mahu-admin-infra`：当前暂无测试类；新增测试优先复用 `mahu-shared` / `mahu-task` 的 Service Registry 模式。

---

## 七、Ebean ORM

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

## 八、Skills 与权威来源

- 仓库内 Skills 位于 `.agents/skills/*/SKILL.md`。
- 与上游对齐与校验见根目录 `skills-lock.json`（`computedHash`）；仓库本地自定义 skill 不强制登记在该文件内。
- 高风险专项流程优先沉淀为 skill；`AGENTS.md` 只保留入口、边界和事实来源，不承载长流程手册。
- 用户提到 Lombok、`record`、模型注解组合、`@Value` / `@Builder` / `@Data` 取舍时，优先使用 `.agents/skills/mahu-lombok/SKILL.md`。
- 用户提到 Helidon、Service Registry、`@Service.Singleton`、`@Service.RunLevel`、Registry 测试装配时，优先使用 `.agents/skills/mahu-helidon-service-registry/SKILL.md`。
- 用户提到部署、发布、镜像、`jib`、`deployDev`、远程主机、上线检查时，优先使用 `.agents/skills/mahu-deployment/SKILL.md`。
- Skills、模块 `README`、`build.gradle`、`docs/` 冲突时，以更接近代码执行事实的内容为准；无法确认时优先代码与配置。
