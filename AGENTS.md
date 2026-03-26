# Mahu 项目 AGENTS 指南

面向在本仓库工作的 AI Agent：提供上下文与约束，减少与项目惯例不一致的回答或改动。

---

## 速览（优先遵守）

| 类别 | 要点 |
|------|------|
| 栈 | Java **25**；Helidon SE **4.4**；EBean；PostgreSQL；Liquibase（`mahu-db`） |
| 坐标 | Gradle `group`：`cool.houge.mahu`（根 `build.gradle`） |
| 格式 | `./gradlew spotlessApply`；CI/自检用 `spotlessCheck`；勿手工对拍 |
| 数据库 | 仅 Liquibase；每处 `changeSet` 须有可行 **rollback**（`mahu-db/README.md`） |
| 生成物 | 勿改 `src/main/gen`、`oas/vo`、`@Generated`、Helidon APT（如 `build/generated/**/*__ServiceDescriptor`）；改 OpenAPI / 模板 / 业务源码 |
| Spotless | 已排除 `**/src/main/gen/**`、`**/build/generated/**`（根 `build.gradle`） |
| 边界 | `mahu-db` 只做 Schema 与变更；业务逻辑在对应业务模块 |
| 语言 | 默认 **简体中文**（用户指定其他语言时从其） |

**Windows**：PowerShell 用 `.\gradlew.bat`；`./gradlew` 适用于 Git Bash / WSL。

---

## 一、背景与文档入口

- **仓库元数据**：`rootProject.name = "mahu"`（`settings.gradle`）。
- **本地依赖**：PostgreSQL、RabbitMQ、MinIO 等 **podman 示例**、**环境简称**（DEV / SIT / UAT / STG / PROD）见根 `README.md`（「开发环境搭建」「应用环境名称规范」）。
- **OpenAPI 前端工具链**：Node / **pnpm** 版本与 Gradle 集成见根 `build.gradle` 的 `node {}`。
- **查阅顺序**（答数据库、接口、环境、配置类问题）：
  1. 根 `README.md`、`mahu-db/README.md`、`changelog-root.yaml`、`docs/content/` 与相关模块代码
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

- **常用**：`build`、`test`（JUnit 5 / `useJUnitPlatform`）、单模块例 `:mahu-domain:test`、`:mahu-web:build`、`spotlessApply`、`spotlessCheck`
- **管理端代码生成**（见 `mahu-admin/mahu-admin-web/build.gradle`、根 `build.gradle`）：  
  `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate`（写入 `mahu-admin-web/src/main/gen`；流程说明见根 `README.md`）
- **镜像与部署**（仅用户明确要求且环境允许）：各模块 `jib`；`deployDev` 含 `ssh` / `podman`，先说明风险再执行
- **文档站点**（根 `README.md`）：`podman run --rm -it -v ./:/docs docker.1ms.run/kevin70/houge-mkdocs-material build`

---

## 四、代码约定

- **包与分层**：`util`、`entity`、`repository`、`service`、`shared`、`controller`（命名后缀见 `README.md` 图示）。
- **SonarLint**：根 `build.gradle`。
- **库表与生成物**：Liquibase / 勿改生成代码 — 见速览表。

---

## 五、Agent 协作原则

- **表达**：简洁；给出路径与模块名；引用现有代码时用工作区约定格式：`起始行:结束行:路径` 代码块。
- **信源**：代码与配置 → `docs/`、`README.md` → 通识（标注假设）。
- **改动**：遵守模块边界；库表或 API 不兼容时写明影响与迁移建议。
- **高风险**：`podman`、`ssh`、远程部署等 — 先说明用途与风险，由用户决定是否执行。

---

## 六、Agent Skills

仓库内：`.agents/skills/*/SKILL.md`。与上游对齐与校验见根目录 `skills-lock.json`（`computedHash`）；以锁文件与 SKILL 正文为准。

细则仍以各模块 `README`、`build.gradle`、`docs/` 为权威来源。

---

## 七、Helidon Service Registry DI 约定（补充）

本仓库使用 Helidon Service Registry（编译时 DI），依赖注入通过 `io.helidon.service.registry.Service` 注解完成。

- **作用域声明（必须）**：服务类使用 `@Service.Singleton` / `@Service.PerLookup` / `@Service.PerRequest` 之一声明作用域。
- **生命周期回调（必须）**：
  - 初始化逻辑放在 `@Service.PostConstruct` 方法中
  - 清理逻辑放在 `@Service.PreDestroy` 方法中
- **启动顺序（必须）**：启动顺序敏感时使用 `@Service.RunLevel`（如 `Service.RunLevel.STARTUP + 1` 这类写法）控制执行先后。
- **依赖注入方式（建议）**：依赖优先通过**构造器注入**实现（常见做法：`final` 字段 + Lombok 生成构造器，或手写构造器），避免字段注入。
- **与仓库现状的兼容说明**：仓库当前未发现 `@Service.Inject` / `@Service.Contract` / `@Inject` 的用法，因此不把它们作为强制约束；如确需使用，请先与现有 Helidon SR 用法对齐并在合入前自检影响范围。
