---
name: openapi-admin
description: 编写或修改 Mahu 管理端 OpenAPI 规范（openapi/admin）时遵循的约定与校验规则。在编辑 admin 接口 YAML、新增 path/schema、或运行 redocly/openApiGenerate 时使用。
---

# OpenAPI Admin 规范

## 规范位置与产物

- **规范根目录**: `openapi/admin/`，入口为 `openapi/admin/openapi.yaml`
- **打包产物**: 在项目根目录执行 `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate` 会先执行 `redocly bundle`，产出 `openapi/dist/mahu-admin-openapi.yaml`，再生成 Java 代码到 `mahu-admin/mahu-admin-web/src/main/gen`（包 `cool.houge.mahu.admin.oas`）
- **勿改生成代码**: 带 `@Generated` 或位于 `oas` 包下的代码不要直接编辑；改接口请改 OpenAPI 规范或模板后重新生成

## 命名与格式（必须遵守）

| 对象 | 规则 | 说明 |
|------|------|------|
| **路径 path** | kebab-case | 如 `/sys/roles`、`/sys/roles/{role_id}` |
| **operationId** | camelCase，且需指定前缀 | 见下方「operationId 前缀」 |
| **参数名** | snake_case | query/path/header 等 |
| **Schema 属性** | snake_case | 所有 `properties` 键名 |

### operationId 允许的前缀

`create`、`upsert`、`update`、`merge`、`delete`、`approve`、`reject`、`get`、`retrieve`、`list`、`page`、`cancel`、`execute`、`login`、`forward`。  
新增操作时需从以上前缀选其一（由 `openapi/redocly.yaml` 中 `rule/operation-id-check` 校验）。

## 文件组织

- **路径**: 每个 path 对应 `openapi/admin/paths/` 下独立 YAML，在根 `openapi.yaml` 的 `paths` 中用 `$ref` 引用
- **Path 文件名**: 与 URL 对应，路径参数用 `_{param}`，例如 `sys_roles.yaml`、`sys_roles_{role_id}.yaml`
- **Schema**: `openapi/admin/components/schemas/`，可复用公共片段（如 `_ordering.yaml`、`BasePageResponse.yaml`）
- **参数**: `openapi/admin/components/parameters/`，如 `page.yaml`、`page_size.yaml`，在 path 中 `$ref` 引用

## 请求与响应

- 仅允许 `application/json` 作为 requestBody 与 response 的 MIME 类型（由 redocly 规则校验）
- 错误响应结构见 `openapi/admin/components/schemas/ErrorResponse.yaml`
- 分页：使用 `BasePageResponse` 的 `allOf` + `items`；查询参数使用 `page`、`page_size` 等公共 parameter 引用

## 权限与扩展

- 每个需要鉴权的 operation 应带 `x-permission-code`（格式如 `SYS_ROLE:R`、`SYS_ROLE:W`）
- 每个 operation 必须带 `tags`，取值为根 `openapi.yaml` 中已定义的 tag（如 `h_role`、`h_admin`）
- 接口说明中的 `x-request-id`、`x-permission-code` 等见 `openapi/admin/info_description.md`

## 校验与构建

- **Lint**: 在 `openapi/` 目录下执行 `pnpm openapi-admin`（或 `redocly lint admin@v1`）进行规范校验
- **打包并生成代码**: 项目根目录执行 `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate`（会先 bundle 再生成）

更多细节与示例见 [reference.md](reference.md)。
