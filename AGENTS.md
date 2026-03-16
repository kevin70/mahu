## Mahu 项目 AGENTS 指南

本文件为在 Mahu 仓库中运行的 AI Agent 提供项目级上下文与协作约定，方便在回答问题或修改代码时有一致的行为。

---

## 一、项目概览

- **项目名称**: Mahu (`rootProject.name = "mahu"`)
- **主要技术栈**:
  - Java 25（见 `build.gradle` 与根目录 `README.md`）
  - Helidon SE（基于 Helidon WebServer 的微服务框架，主要用于 `mahu-web`、`mahu-admin`）
  - EBean ORM（数据库访问）
  - PostgreSQL（主数据库）
  - Liquibase（数据库 Schema 版本管理，位于 `mahu-db` 模块）
  - Node.js + pnpm（用于文档与 OpenAPI 相关前端工具）

---

## 二、模块结构（来自 `settings.gradle`）

- **核心模块**
  - `mahu-base`：基础设施与通用代码
  - `mahu-config`：配置相关与配置装配逻辑
  - `mahu-domain`：领域层（EBean 实体 + 共享业务逻辑，遵循根 `README.md` 中的 Java Package 规范）
- `mahu-web`：Web / API 服务（Helidon SE WebServer，依赖 `mahu-base`、`mahu-domain` 等）
- **业务模块**
  - `mahu-admin:mahu-admin-infra`：管理端后台基础设施
  - `mahu-admin:mahu-admin-web`：管理端 Web 应用，使用 OpenAPI Generator 生成部分代码（`src/main/gen`）
- **数据库与代码生成**
  - `mahu-db`：数据库 Schema 与变更脚本（Liquibase，`src/main/resources/db/changelog`）
  - `mahu-codegen`：代码生成相关逻辑（如有需要可进一步查看）
- **文档与开放接口**
  - `docs`：项目文档与设计规范（数据库设计、AI、通用设计等）
  - `openapi`：OpenAPI 规范与相关工具（`openapi/dist/mahu-admin-openapi.yaml` 等）

---

## 三、文档与规范入口

- **开发准备与环境说明**: 根目录 `README.md`
- **数据库规范与设计**:
  - `mahu-db/README.md`：Liquibase 使用原则（每个 `changeSet` 必须有 `rollback`）
  - `mahu-db/src/main/resources/db/changelog/changelog-root.yaml`：数据库所有变更入口，引用各 `*.sql` 变更文件
- **系统文档与设计说明**:
  - `docs/content/`：包含数据库设计、通用设计、AI 相关说明等

在回答与数据库、接口规范、环境配置相关的问题时，应优先查阅上述文档文件中的内容，再进行总结与推理。

---

## 四、开发与构建（给 Agent 的操作指引）

- **基础构建与测试**（Gradle 多模块项目，根目录执行）:
  - 构建项目: `./gradlew build`
  - 运行测试: `./gradlew test`
- **OpenAPI 与管理端相关任务**（来自 `mahu-admin/mahu-admin-web/build.gradle` 与根 `build.gradle`）:
  - 安装 OpenAPI 工具依赖: `./gradlew openapiDepsInstall`
  - 生成管理端 OpenAPI 服务器端代码: `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate`
- **镜像构建与部署**（仅在用户明确要求时参考）:
  - `mahu-admin` 容器镜像通过 `jib` 构建（见 `mahu-admin/mahu-admin-web/build.gradle` 的 `jib` 配置）
  - 存在 `deployDev` 任务用于部署到 `dev01` 服务器（执行涉及远程 `ssh` 与 `podman`，只能在用户明确要求且环境允许的情况下调用）
- **文档构建**:
  - 根 `README.md` 示例命令使用 `podman` 构建文档站点：
    - `podman run --rm -it -v ./:/docs docker.1ms.run/kevin70/houge-mkdocs-material build`

在建议或执行命令之前，应确认命令确实在对应的 `build.gradle` 或文档中存在，避免凭空创造 Gradle Task 名称。

---

## 五、代码风格与约定（Agent 需要遵守）

- **Java 版本**: 统一使用 Java 25（`sourceCompatibility` / `targetCompatibility`）
- **代码格式化**: 用 `./gradlew spotlessApply`，勿手工改格式。
- **包结构规范**（来自根 `README.md`）:
  - `util`：工具类
  - `entity`：与数据库表一一对应的数据实体
  - `repository`：数据库访问对象（类名以 `Repository` 结尾）
  - `service`：业务服务对象（类名以 `Service` 结尾）
  - `shared`：共享内部公共业务逻辑
  - `controller`：REST 接口对象（类名以 `Controller` 结尾）
- **静态检查与生成代码**: SonarLint（见根 `build.gradle`）。不直接编辑生成代码（`oas`、带 `@Generated` 的类）；除非用户明确要求，优先改 OpenAPI 规范或模板。
- **数据库变更**:
  - 所有数据库变更应通过 Liquibase `changeSet` 管理，并在 `mahu-db` 模块中添加对应 SQL 文件。
  - 每个 `changeSet` 必须包含可行的 `rollback` 语句，保持与 `mahu-db/README.md` 的原则一致。

---

## 六、AI Agent 回答与改动的原则

- **语言与风格**:
  - 默认使用 **简体中文** 回答，除非用户明确要求英文或特定语言。
  - 回答时保持简洁、信息密度高，必要时引用具体文件路径与模块名称。
- **查找信息的优先顺序**:
  1. 已存在的代码与配置（Java / SQL / YAML / Gradle）
  2. 项目内的文档（`docs/`、`README.md` 系列）
  3. 在缺少信息时，再结合通用最佳实践进行合理推断，并在回答中说明是假设。
- **修改代码时**:
  - 避免破坏模块边界（例如 `mahu-db` 只做数据库相关变更，不直接承担业务逻辑）。
  - 避免修改生成代码（`src/main/gen`、`oas/vo` 等），除非用户有特殊说明。
  - 如有可能引入数据库或接口不兼容变更，应在回答中明确指出影响及建议的迁移方式。
- **命令执行与外部依赖**:
  - 涉及 `podman`、远程 `ssh`、`jib` 等对环境有较强依赖的操作时，应在执行前于回答中清晰说明作用与风险，让用户自主决定是否执行。

---

## 七、可扩展说明

如果后续在 `.cursor/rules/` 或其他位置新增更细粒度的规则文件，本 `AGENTS.md` 可以作为全局入口，简要列出各模块 / 子系统的专属规则与链接，方便 Agent 在回答时快速定位到更具体的约束。

