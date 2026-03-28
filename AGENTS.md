# Mahu 项目 AGENTS 指南

面向在本仓库工作的 AI Agent：提供上下文与约束，减少与项目惯例不一致的回答或改动。

---

## 一、背景与文档入口

- **仓库元数据**：`rootProject.name = "mahu"`（`settings.gradle`）。
- **本地依赖**：PostgreSQL、RabbitMQ、MinIO 等 **podman 示例**、**环境简称**（DEV / SIT / UAT / STG / PROD）见根 `README.md`（「开发环境搭建」「应用环境名称规范」）。
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

**代码生成：**
- `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate` - OpenAPI 代码生成（写入 `src/main/gen`；流程见根 `README.md`）

**镜像与部署（风险操作，需用户确认）：**
- 各模块 `jib` - 构建镜像
- `deployDev` - 部署到开发环境（含 `ssh` / `podman`，先说明风险）

**文档：**
- `podman run --rm -it -v ./:/docs docker.1ms.run/kevin70/houge-mkdocs-material build` - 构建文档站点（见根 `README.md`）

---

## 四、Agent 协作原则

- **表达**：简洁；给出路径与模块名；引用代码时格式：`起始行:结束行:路径`。
- **信源**：代码与配置 → `docs/`、`README.md` → 通识（标注假设）。
- **改动**：遵守模块边界；库表或 API 不兼容时写明影响与迁移建议。
- **高风险**：`podman`、`ssh`、远程部署等 — 先说明用途与风险，由用户决定是否执行。

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
