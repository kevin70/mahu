# Mahu 项目 AGENTS 指南

面向在本仓库工作的 AI Agent：提供上下文与约束，减少与项目惯例不一致的回答或改动。

---

## 一、背景与文档入口

- **仓库元数据**：`rootProject.name = "mahu"`（`settings.gradle`）。
- **本地依赖**：PostgreSQL、MinIO 等 **Docker 示例**、**环境简称**（DEV / SIT / UAT / STG / PROD）见根 `README.md`（「开发环境搭建」「应用环境名称规范」）。
- **OpenAPI 前端工具链**：Node / **pnpm** 版本与 Gradle 集成见根 `build.gradle` 的 `node {}`。
- **查阅顺序**（答数据库、接口、环境、配置类问题）：
  1. 根 `README.md`、`changelog-root.yaml`、`docs/content/` 与相关模块代码
  2. 再总结；缺信息时的推断须标明为假设

---

## 二、模块一览（`settings.gradle`）

| 模块 | 说明 |
|------|------|
| `mahu-bom` | Java Platform / BOM；与 `gradle/libs.versions.toml` 一起约束版本 |
| `mahu-base` | 基础设施与通用代码 |
| `mahu-domain` | 领域层、EBean；包结构见根 `README.md`「Java Package 规范」 |
| `mahu-codegen` | 代码生成相关 |
| `mahu-db` | Liquibase：`src/main/resources/db/changelog`，入口 `changelog-root.yaml` |
| `mahu-dal` | 数据访问层（Repository 等） |
| `mahu-shared` | 跨应用共享的领域/应用服务 |
| `mahu-web` | 工具型 Web 能力（Helidon WebServer 相关封装/基础能力） |
| `mahu-admin:mahu-admin-infra` | 管理端基础设施 |
| `mahu-admin:mahu-admin-web` | 管理端 Helidon SE Web/API 应用；OpenAPI Generator → `src/main/gen` |
| `mahu-task` | 独立定时任务服务（db-scheduler） |
| `docs`、`openapi` | 设计文档与规范资产（如 `openapi/dist/mahu-admin-openapi.yaml`） |

---

## 三、开发与构建

根目录执行；引用 Task 前请在对应 `build.gradle` 或文档中核对，**勿臆造 Task 名**。

**编译与测试：**
- `./gradlew build` - 完整构建
- `./gradlew test` - 全量测试
- `./gradlew :mahu-domain:test` - 单模块测试（示例）
- `./gradlew spotlessCheck` - 格式检查
- `./gradlew spotlessApply` - 格式化代码

**单元测试通用规则：**
- 测试需具备隔离性：每个测试方法独立初始化测试上下文，不共享可变状态，避免用例间相互污染。
- 被测对象优先通过项目既有 DI/测试基线注入；避免手工 `new` 组装复杂依赖，依赖替身统一在用例初始化阶段完成绑定。
- Mock 约定优先使用 Mockito；mock 字段建议使用 `private final`，并在每个用例开始前完成装配。
- 测试方法命名采用 `行为_结果` 风格，确保名称可直接表达测试意图。
- 用例覆盖至少包含：一条成功路径 + 一条失败或边界路径（如命中/未命中、可见/不可见、可更新/不可更新）。
- 涉及时间、排序、ID 等易波动断言时使用稳定输入（如 `Instant.parse(...)`、显式排序断言、负数业务 ID 约定）。
- 查询类测试至少验证“过滤条件生效 + 排序规则生效”；更新类测试至少验证“返回影响行数 + 落库状态一致”。
- 各模块测试细节（基类、容器、框架注入方式）以模块 `README`、现有测试范式和 `build.gradle` 为准。

**项目单元测试使用现状（按模块）：**
- `mahu-dal`：以 `PostgresLiquibaseTestBase` 为统一基线，使用 Testcontainers PostgreSQL + Liquibase（`sit` 标签）初始化库表；每个用例开启事务并默认回滚，适合仓储层真实 SQL 行为验证。
- `mahu-dal`：数据构造以 Instancio `Model` 为主（忽略 `id/createdAt/updatedAt/version` 等波动字段），断言重点为“查询过滤 + 排序”“状态迁移前置条件 + 落库结果”。
- `mahu-shared`：以 `@Testing.Test(perMethod = true)` + `Services.set(...)` 为标准模式，在 `@BeforeEach` 绑定 mock，测试方法参数直接注入被测 Service。
- `mahu-task`：与 `mahu-shared` 一致采用 Helidon Testing + Service Registry 注入；针对 Worker 场景优先测试 `executeAt(Instant)` 这类可控时间入口，避免不稳定的调度线程副作用。
- `mahu-web`：当前为纯 JUnit + Mockito 轻量单测（无容器/无数据库），重点验证异常匹配、错误响应映射与回退逻辑。
- `mahu-domain`：当前仅提供测试环境 DI 组件（`TestDataSourceProvider`、`TestDatabaseProvider`），用于为依赖模块提供测试期 DataSource/Database 覆盖。
- `mahu-admin:mahu-admin-infra`：目前仅有测试资源（`meta-config.yaml`、`mahu-admin-test.yaml`），暂无测试类；新增测试时应优先复用 `mahu-shared/mahu-task` 的 Service Registry 模式。

**推荐最佳实践（汇总）：**
- **分层选型**：仓储层优先“真实数据库集成测试”（`mahu-dal` 模式）；服务层/Worker 优先“DI 注入 + Mockito 替身单测”（`mahu-shared` / `mahu-task` 模式）；纯工具逻辑优先“无容器轻量单测”（`mahu-web` 模式）。
- **DI 优先**：在 Helidon 服务测试中，优先通过 `@Testing.Test(perMethod = true)` + `Services.set(...)` 绑定依赖，再通过方法参数注入被测对象；减少手工装配导致的生命周期偏差。
- **稳定输入**：时间断言固定 `Instant.parse(...)`；业务数值 ID 建议使用负数；排序断言必须显式比较顺序，避免依赖默认顺序。
- **边界覆盖**：状态机/调度类至少覆盖“成功迁移、前置条件不满足、异常分支、空输入分支”；查询类至少覆盖“命中/未命中”。
- **配置可控**：测试资源通过 `meta-config.yaml` + `application-test.yaml` 管理，定时任务相关配置在测试中默认关闭（如字典/功能开关缓存刷新），避免后台调度干扰断言。
- **可追踪性**：每个新增测试应能回答“验证了哪条业务规则”，并在方法名直接体现规则与结果，避免只有路径覆盖没有行为语义。

**命名规则与优化建议：**
- **包与分层命名**：遵循根 `README.md` 的 Java Package 规范（`entity/repository/service/controller/shared/util`），其中 `Repository`、`Service`、`Controller` 后缀为必选约定。
- **Java 标识符命名**：以 `checkstyle.xml` 为准：包名全小写点分、成员/局部变量 camelCase、方法名 camelCase（允许下划线用于测试语义）。新增规则先改 Checkstyle 再改代码。
- **测试命名**：测试方法统一 `行为_结果`，并优先表达业务语义而非技术细节；同类场景建议用一致前缀（如 `executeAt_...`、`handle_...`）。
- **主题与状态常量**：业务枚举值（如 `DelayedTaskTopics.topic()`）禁止硬编码字符串，生产代码与测试代码均应通过常量/枚举获取，避免命名漂移。
- **数据库迁移命名**：延续 `VYYYYMMDD_NNN__description.sql`，`description` 统一使用小写下划线短语；新增迁移文件按时间与序号递增，避免并行分支冲突。
- **配置键命名**：配置 key 统一 `kebab-case`（如 `feature-flag-cache-refresh`），按领域分组（`db.*`、`scheduling.*`），禁止同一层级混用 `camelCase`/`snake_case`。

**代码生成：**
- `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate` - OpenAPI 代码生成（写入 `src/main/gen`；流程见根 `README.md`）

- **镜像与部署（风险操作，需用户确认）：**
- 各模块 `jib` - 构建镜像
- `deployDev` - 部署到开发环境（含 `ssh` / `docker`，先说明风险）

**文档：**
- `docker run --rm -it -v ./:/docs docker.1ms.run/kevin70/houge-mkdocs-material build` - 构建文档站点（见根 `README.md`）

---

## 四、Agent 协作原则

- **表达**：简洁；给出路径与模块名；引用代码时格式：`起始行:结束行:路径`。
- **语言**：默认使用中文输出与生成内容（包括说明、注释、提交信息、评审意见等）；仅在用户明确要求或上下文必须时使用英文。
- **信源**：代码与配置 → `docs/`、`README.md` → 通识（标注假设）。
- **改动**：遵守模块边界；库表或 API 不兼容时写明影响与迁移建议。
- **高风险**：`docker`、`ssh`、远程部署等 — 先说明用途与风险，由用户决定是否执行。

---

## 五、Agent Skills

仓库内：`.agents/skills/*/SKILL.md`。与上游对齐与校验见根目录 `skills-lock.json`（`computedHash`）；以锁文件与 SKILL 正文为准。

细则仍以各模块 `README`、`build.gradle`、`docs/` 为权威来源。

---

## 六、Helidon Service Registry DI 约定

本仓库使用 Helidon Service Registry（编译时 DI），通过 `io.helidon.service.registry.Service` 注解完成依赖注入。

- **作用域**：`@Service.Singleton` / `@Service.PerLookup` / `@Service.PerRequest` 三者之一
- **生命周期**：`@Service.PostConstruct`（初始化）、`@Service.PreDestroy`（清理）
- **启动顺序**：`@Service.RunLevel` 控制（如 `RunLevel.STARTUP + 1`）
- **注入方式**：优先使用构造器注入（`final` 字段 + Lombok 生成器）
