# Mahu 项目 AGENTS 指南

面向在本仓库工作的 AI Agent：统一信息来源、模块边界、编码习惯与测试约定，减少与项目现状不一致的回答或改动。

---

## 一、信息入口与查阅顺序

- **仓库元数据**：`rootProject.name = "mahu"`（`settings.gradle`）。
- **本地依赖**：PostgreSQL、MinIO 等本地启动示例见 `docs/content/1.getting-started/2.installation.md`。
- **环境简称**：DEV / SIT / UAT / STG / PROD，见 `docs/content/2.architecture/1.metadata-dictionary.md` 中「环境名称」。
- **OpenAPI 前端工具链**：Node / `pnpm` 版本与 Gradle 集成见根 `build.gradle` 的 `node {}`。
- **查阅优先级**：
  1. `docs/content/`、相关模块代码、`mahu-db/src/main/resources/changelog-root.yaml`
  2. 根 `README.md`（仅入口级信息）
  3. 再总结；缺信息时的推断必须标明为假设

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

## 三、常用命令

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

### 风险操作

- `./gradlew :mahu-admin:mahu-admin-web:jib`：构建管理端镜像
- `./gradlew :mahu-admin:mahu-admin-web:deployDev`：部署到开发环境
- 涉及 `docker`、`ssh`、远程部署等高风险操作时，必须先说明用途与风险，再由用户决定是否执行

---

## 四、工作原则

- **语言**：默认使用中文输出与生成内容，包括说明、注释、提交信息、评审意见；仅在用户明确要求或上下文必须时使用英文。
- **表达**：简洁直接；说明中优先给出模块名、路径和关键约束；引用代码时使用 `起始行:结束行:路径`。
- **信源**：代码与配置优先于文档，文档优先于 `README.md`，通识最后使用。
- **模块边界**：改动必须遵守 `docs/content/2.architecture/4.module-boundaries.md` 中的模块与包边界。
- **兼容性**：涉及库表或 API 不兼容变更时，必须写明影响范围与迁移建议。

---

## 五、代码约定

### 命名与结构

- **包与分层命名**：遵循 `docs/content/1.getting-started/1.introduction.md` 与 `docs/content/2.architecture/4.module-boundaries.md` 的 Java Package / 模块边界规范（`entity/repository/service/controller/shared/util`）。
- **后缀约定**：`Repository`、`Service`、`Controller` 后缀为必选约定。
- **Java 标识符**：以 `checkstyle.xml` 为准；包名全小写点分，成员/局部变量 camelCase，方法名 camelCase；测试方法允许下划线表达语义。
- **业务常量**：主题、状态、枚举值禁止硬编码字符串；生产代码与测试代码都应通过常量或枚举获取。
- **数据库迁移命名**：沿用 `VYYYYMMDD_NNN__description.sql`；`description` 使用小写下划线短语；文件按时间与序号递增。
- **配置键命名**：统一 `kebab-case`，按领域分组（如 `db.*`、`scheduling.*`），禁止同层级混用 `camelCase` / `snake_case`。

### Lombok 使用规范

- **总原则**：对外可见、跨类可见、跨模块共享的模型，**Lombok 优先于 `record`**；仅当类型是类内部 `private` / `private static` 辅助类型时，可忽略此规则。
- **不可变模型**：`query`、只读 `result`、共享不可变模型、值对象优先 `@Value`；需要构建器时加 `@Builder`；有默认值时用 `@Builder.Default`。
- **可变模型**：`command`、表单、请求/响应 bean 优先 `@Getter/@Setter`；只有确认是简单可变数据载体时才使用 `@Data`。
- **依赖注入类**：Service / Controller / Handler / Interceptor 统一使用 `final` 字段 + 构造器注入；新增代码优先 `@RequiredArgsConstructor`，`@AllArgsConstructor` 次之。
- **实体与工具类**：EBean Entity 继续使用 `@Getter/@Setter`；纯工具类优先 `@UtilityClass`。
- **最小注解集**：只使用贴合语义的最小 Lombok 组合；不要叠加语义重复的注解；已有显式业务构造器时，不要为了统一风格强行补 Lombok 构造器。
- **额外约束**：敏感字段类型谨慎使用 `@Data` 或自动 `toString`；生成代码不强制套用本规范；当前 `mahu-model.query` 与 `mahu-shared` 只读共享模型，以 `@Value + @Builder` 为默认参考。

### Helidon Service Registry DI

- 本仓库使用 Helidon Service Registry（编译时 DI），通过 `io.helidon.service.registry.Service` 注解完成依赖注入。
- **作用域**：`@Service.Singleton` / `@Service.PerLookup` / `@Service.PerRequest`
- **生命周期**：`@Service.PostConstruct`、`@Service.PreDestroy`
- **启动顺序**：需要时通过 `@Service.RunLevel` 控制
- **注入方式**：优先构造器注入（`final` 字段 + 构造器 / Lombok 构造器）

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
- 具体测试细节仍以模块 `README`、现有测试范式和 `build.gradle` 为准。

### 按模块选型

- `mahu-dal`：优先真实数据库集成测试，基线为 `PostgresLiquibaseTestBase`；使用 Testcontainers PostgreSQL + Liquibase（`sit` 标签），每个用例开启事务并默认回滚。
- `mahu-dal`：数据构造以 Instancio `Model` 为主，忽略 `id/createdAt/updatedAt/version` 等波动字段。
- `mahu-shared`：优先 `@Testing.Test(perMethod = true)` + `Services.set(...)`，通过 Service Registry 注入被测对象。
- `mahu-task`：与 `mahu-shared` 一致，优先测试 `executeAt(Instant)` 这类可控入口，避免调度线程副作用。
- `mahu-web`：优先纯 JUnit + Mockito 轻量单测，重点验证异常匹配、错误响应映射与回退逻辑。
- `mahu-entity`：当前提供测试环境 DI 组件，用于为依赖模块提供测试期 DataSource / Database 覆盖。
- `mahu-admin:mahu-admin-infra`：当前暂无测试类；新增测试优先复用 `mahu-shared` / `mahu-task` 的 Service Registry 模式。

### 测试最佳实践

- **分层选型**：仓储层优先真实数据库集成测试；服务层 / Worker 优先 DI + Mockito 替身单测；纯工具逻辑优先轻量单测。
- **Helidon 优先**：已依赖 `helidon-service-registry` 的模块，测试默认优先 Service Registry 模式，不回退到手工装配。
- **注解即强制**：测试类一旦使用 Helidon Testing / Service Registry 注解，就必须延续该模式。
- **稳定输入**：时间断言固定 `Instant.parse(...)`；排序断言必须显式比较顺序。
- **边界覆盖**：状态机 / 调度类至少覆盖成功迁移、前置条件不满足、异常分支、空输入；查询类至少覆盖命中 / 未命中。
- **配置可控**：测试资源统一通过 `meta-config.yaml` + `application-test.yaml` 管理；测试中默认关闭会干扰断言的后台刷新类配置。
- **可追踪性**：每个新增测试都应能回答“验证了哪条业务规则”。

---

## 七、Skills 与权威来源

- 仓库内 Skills 位于 `.agents/skills/*/SKILL.md`。
- 与上游对齐与校验见根目录 `skills-lock.json`（`computedHash`）。
- Skills、模块 `README`、`build.gradle`、`docs/` 冲突时，以更接近代码执行事实的内容为准；无法确认时优先代码与配置。
