# OpenAPI Admin 规范参考

## Redocly 规则摘要（openapi/redocly.yaml）

- `paths-kebab-case`: path 必须 kebab-case
- `operation-operationId`: 必须有 operationId
- `operation-id-check`: operationId 必须以规定前缀开头
- `parameter-casing` / `schema-properties-casing`: 参数与 schema 属性为 snake_case
- `request-mime-type` / `response-mime-type`: 仅允许 `application/json`
- `operation-singular-tag`: 建议每个 operation 单一 tag
- `operation-tag-defined`: tag 必须在根 openapi 的 `tags` 中定义
- `no-unused-components`: 不允许未引用的 components
- `path-not-include-query`: path 中不得包含 query 部分
- `no-ambiguous-paths`: 路径不可歧义

自定义插件 `plugins/local.js` 提供 `checkWordsStarts`（operationId 前缀）等断言。

## 路径与文件对应示例

| OpenAPI path | 文件 |
|--------------|------|
| `/sys/roles` | `paths/sys_roles.yaml` |
| `/sys/roles/{role_id}` | `paths/sys_roles_{role_id}.yaml` |
| `/sys/admin-logs/{type}` | `paths/sys_admin-logs_{type}.yaml` |
| `/p/dicts/{dc}` | `paths/p_dicts_{dc}.yaml` |

根 `openapi.yaml` 中引用方式：`$ref: "paths/sys_roles.yaml"`。

## 分页约定

- 列表/分页接口：`operationId` 使用 `page*`（如 `pageSysRole`）
- 查询参数：引用 `page`、`page_size`（及可选 `include_total`、`sort`、`filter` 等）
- 响应 Schema：`allOf` 引用 `BasePageResponse`，并定义 `items` 数组，例如：

```yaml
# SysRolePageResponse.yaml
type: object
description: 角色分页列表
allOf:
  - $ref: "./BasePageResponse.yaml"
properties:
  items:
    type: array
    items:
      $ref: "./SysRoleResponse.yaml"
```

## 单资源 CRUD 命名

- 创建: `create*`，POST，通常 204
- 分页: `page*`，GET，200 + PageResponse
- 单条: `get*`，GET，200 + 单资源 Schema
- 更新: `update*`，PUT，通常 204
- 删除: `delete*`，DELETE，通常 204

RequestBody 命名：创建与更新请求体相同时用 `*UpsertRequest`；仅创建用 `*CreateRequest`，仅更新用 `*UpdateRequest`。

## Tag 与分组

根 `openapi.yaml` 中通过 `tags` 定义 tag（如 `h_role`），并用 `x-displayName` 提供展示名；`x-tagGroups` 对 tag 分组（如「基础模块」「系统模块」）。新增接口时在现有 tag 中选用，或先在根中新增 tag 再使用。

## 生成代码流程

执行 `./gradlew :mahu-admin:mahu-admin-web:openApiGenerate` 即可（会自动完成依赖安装与 bundle）。生成代码位于 `mahu-admin/mahu-admin-web/src/main/gen`，包名 `cool.houge.mahu.admin.oas`，controller 与 vo 勿手改。
