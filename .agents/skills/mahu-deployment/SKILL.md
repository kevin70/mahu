---
name: mahu-deployment
description: 处理 Mahu 仓库中的部署、发布、镜像构建、`jib`、`deployDev`、远程执行、上线检查等请求时必须使用。该 skill 只适用于当前仓库已存在的管理端部署链路；如果用户提到部署到 dev、构建或推送管理端镜像、执行远程发布、核对部署前置条件、解释现有部署流程，即使用户没有明确说“skill”，也要主动使用本 skill。
---

# Mahu Deployment

用于处理 Mahu 仓库里已有的部署链路。目标是先核对仓库事实，再向用户暴露风险、阻塞项和可执行入口；不要替仓库补全并不存在的部署方案。

## 适用范围

- 只覆盖当前已确认存在的管理端部署链路：
  - `:mahu-admin:mahu-admin-web:jib`
  - `:mahu-admin:mahu-admin-web:deployDev`
- 不覆盖：
  - `mahu-task` 的部署流程
  - 新环境、新模块、新注册表或新编排方式
  - 仓库中不存在的 Helm、Kubernetes、CI/CD 流程

## 先核对的事实来源

按以下顺序查：

1. 根 `build.gradle`
   - `remotes` 中的远程主机、用户和 SSH 身份文件
   - 根级插件与仓库级约束
2. `mahu-admin/mahu-admin-web/build.gradle`
   - `jib` 镜像名、标签、容器端口、JVM 参数
   - `deployDev` 的依赖关系和远程执行命令
3. `docs/content/`
   - 环境简称、安装说明、运行前置条件
4. 根 `AGENTS.md`
   - 高风险操作边界和协作约束

如果这些来源之间存在冲突，以更接近执行事实的代码与配置为准。

## 当前已知仓库事实

- 根 `build.gradle` 当前定义了 `remotes.dev01`，主机为 `mahu.houge.cool`，用户为 `root`，身份文件为 `~/.ssh/id_rsa`。
- `mahu-admin/mahu-admin-web/build.gradle` 当前定义：
  - `jib` 目标镜像：`registry.cn-hangzhou.aliyuncs.com/houge-cool/mahu-admin`
  - 标签：`$project.version`、`latest`
  - 容器端口：`9090`
  - `deployDev` 依赖 `jib`
  - `deployDev` 会在远端执行 `docker pull`、`docker stop`、`docker rm`、`docker run`
- **已发现阻塞项**：`deployDev` 当前使用 `session(remotes.dev)`，但根 `build.gradle` 中可见的是 `remotes.dev01`。这属于执行前必须先向用户暴露的事实；不要自行猜测别名、也不要在未获授权时顺手修复。

## 工作方式

### 1. 先判断用户是在“问流程”还是“要执行”

- 如果用户是在问部署怎么做：
  - 输出已核对到的事实来源
  - 给出建议命令和前置检查
  - 明确列出风险和阻塞项
  - 不直接执行
- 如果用户是在要求执行部署：
  - 先说明用途、目标环境、影响范围、前置条件、回滚面
  - 明确指出当前阻塞项或不确定项
  - 只有在用户明确确认后，才执行高风险命令

### 2. 不要扩展仓库里不存在的流程

- 不要发明新的 deploy task、镜像仓库、环境名或启动命令。
- 不要假设 `dev01` 就等于 `dev`，除非仓库中已有明确映射或用户明确确认。
- 不要把“如何修复部署脚本”与“立即执行部署”混为一谈。

### 3. 高风险动作必须先确认

以下动作默认都视为高风险：

- 执行 `./gradlew :mahu-admin:mahu-admin-web:jib`
- 执行 `./gradlew :mahu-admin:mahu-admin-web:deployDev`
- 任何 `docker`、`ssh`、远程主机操作
- 任何镜像推送、容器替换、远端服务停止/删除/重启

在执行前，必须先告诉用户：

- 目标模块和目标环境
- 会触发的实际动作
- 当前已知阻塞项
- 失败后的直接影响

## 输出格式

默认按以下结构输出，保持简洁：

### 已核对事实

- 列出实际读取到的文件和关键结论

### 风险与阻塞项

- 列出高风险动作
- 列出当前配置不一致或缺失的地方

### 建议命令

- 只给出仓库中真实存在的命令

### 是否需要用户确认

- 如果涉及高风险执行，明确写出“需要用户确认后才能执行”

## 示例

### 用户问流程

用户：`管理端现在怎么部署到 dev？`

期望行为：

- 说明当前存在 `jib` 与 `deployDev`
- 说明 `deployDev` 依赖 `jib`
- 指出 `remotes.dev` 与 `remotes.dev01` 的不一致
- 给出建议先核对远程别名，再决定是否执行

### 用户要求执行

用户：`帮我把管理端发到 dev`

期望行为：

- 先说明这是高风险动作
- 先列出会发生的镜像构建 / 推送 / 远程 Docker 替换
- 指出当前 remote 别名不一致，属于执行阻塞项
- 在未获得进一步确认前，不直接运行部署命令
