# OpenAPI Component 规范

良好的组件命名能显著提升 API 文档的可读性和可维护性。以下是详细的命名规范和最佳实践：

## 一、Schema 组件命名规范

### 1. 基础数据类型

```yaml
components:
  schemas:
    # 通用基础类型
    UUID:
      type: string
      format: uuid
      example: '550e8400-e29b-41d4-a716-446655440000'

    Email:
      type: string
      format: email
      example: 'user@example.com'
```

### 2. 请求体命名

| 类型     | 命名模式              | 示例                     |
| -------- | --------------------- | ------------------------ |
| 创建请求 | `<实体>CreateRequest` | `UserCreateRequest`      |
| 更新请求 | `<实体>UpdateRequest` | `ProductUpdateRequest`   |
| 查询请求 | `<实体>QueryRequest`  | `OrderQueryRequest`      |
| 批量操作 | `Bulk<操作>Request`   | `BulkDeleteUsersRequest` |

```yaml
components:
  schemas:
    UserCreateRequest:
      type: object
      properties:
        username: { type: string }
        email: { $ref: '#/components/schemas/Email' }

    ProductUpdateRequest:
      type: object
      properties:
        name: { type: string }
        price: { type: number }
```

### 3. 响应体命名

| 类型     | 命名模式               | 示例                   |
| -------- | ---------------------- | ---------------------- |
| 基础响应 | `<实体>Response`       | `UserResponse`         |
| 详情响应 | `<实体>DetailResponse` | `OrderDetailResponse`  |
| 列表响应 | `<实体>ListResponse`   | `ProductListResponse`  |
| 分页响应 | `<实体>PagedResponse`  | `ArticlePagedResponse` |
| 操作结果 | `<操作>Result`         | `DeleteResult`         |
| 错误响应 | `ErrorResponse`        | （全局统一错误格式）   |

```yaml
components:
  schemas:
    UserResponse:
      type: object
      properties:
        id: { $ref: '#/components/schemas/UUID' }
        username: { type: string }
        createdAt: { type: string, format: date-time }

    ProductListResponse:
      type: array
      items:
        $ref: '#/components/schemas/ProductResponse'

    ErrorResponse:
      type: object
      properties:
        code: { type: string }
        message: { type: string }
        details: { type: array, items: { type: string } }
```

## 二、参数组件命名规范

### 1. 通用参数

```yaml
components:
  parameters:
    # 分页参数
    PageNumber:
      name: page
      in: query
      schema:
        type: integer
        minimum: 1
        default: 1

    PageSize:
      name: pageSize
      in: query
      schema:
        type: integer
        minimum: 1
        maximum: 100
        default: 20

    # ID参数
    UserIdInPath:
      name: userId
      in: path
      required: true
      schema:
        $ref: '#/components/schemas/UUID'
```

### 2. 命名模式

| 类型     | 命名模式         | 示例              |
| -------- | ---------------- | ----------------- |
| 路径参数 | `<实体>IdInPath` | `ProductIdInPath` |
| 查询参数 | `<字段>InQuery`  | `StatusInQuery`   |
| 排序参数 | `SortBy<字段>`   | `SortByCreatedAt` |

## 三、响应组件命名规范

### 1. 通用响应

```yaml
components:
  responses:
    # 成功响应
    SuccessResponse:
      description: 操作成功
      content:
        application/json:
          schema:
            type: object
            properties:
              success: { type: boolean, example: true }
              data: { type: object }

    # 错误响应
    BadRequestError:
      description: 参数错误
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          examples:
            invalidEmail:
              value:
                code: 'INVALID_EMAIL'
                message: '邮箱格式不正确'
```

### 2. 命名模式

| 类型     | 命名模式                | 示例                    |
| -------- | ----------------------- | ----------------------- |
| 成功响应 | `Success<操作>Response` | `SuccessCreateResponse` |
| 错误响应 | `<错误类型>Error`       | `NotFoundError`         |
| 验证错误 | `ValidationError`       | （表单验证专用）        |

## 四、示例组件命名规范

### 1. 请求/响应示例

```yaml
components:
  examples:
    # 用户相关
    UserCreateExample:
      summary: 用户创建示例
      value:
        username: 'john_doe'
        email: 'john@example.com'
        password: 'P@ssw0rd123'

    UserResponseExample:
      summary: 用户响应示例
      value:
        id: '550e8400-e29b-41d4-a716-446655440000'
        username: 'john_doe'
        email: 'john@example.com'
        createdAt: '2023-01-01T00:00:00Z'
```

### 2. 命名模式

| 类型     | 命名模式                | 示例                   |
| -------- | ----------------------- | ---------------------- |
| 请求示例 | `<实体><操作>Example`   | `ProductUpdateExample` |
| 响应示例 | `<实体>ResponseExample` | `OrderResponseExample` |
| 错误示例 | `<错误类型>Example`     | `ConflictErrorExample` |

## 五、最佳实践建议

1. 一致性：整个 API 使用相同的命名约定
2. 自描述性：名称应清晰表达组件用途
3. 尽量避免缩写：使用完整单词（如 Authentication 而非 Auth）
4. 分层命名：
   - 请求：`<领域><操作>Request`
   - 响应：`<领域><类型>Response`
5. 版本控制：当 Schema 有重大变更时考虑添加版本后缀（如 UserV2）

## 六、完整示例

```yaml
components:
    schemas:
        # 基础类型
        Timestamp:
            type: string
            format: date-time

        # 请求体
        ArticleCreateRequest:
            type: object
            required: [ title, content ]
            properties:
                title: { type: string, maxLength: 100 }
                content: { type: string }
                tags: { type: array, items: { type: string } }

        # 响应体
        ArticleResponse:
            type: object
            properties:
                id: { type: string }
                title: { type: string }
                content: { type: string }
                createdAt: { $ref: '#/components/schemas/Timestamp' }

        ArticleListResponse:
            type: array
            items:
                $ref: '#/components/schemas/ArticleResponse'

    parameters:
        ArticleIdInPath:
            name: articleId
            in: path
            required: true
            schema:
                type: string

    responses:
        ArticleNotFoundError:
            description: 文章不存在
            content:
                application/json:
                    schema:
                        $ref: '#/components/schemas/ErrorResponse'
                    examples:
                        notFound:
                            value:
                                code: "ARTICLE_NOT_FOUND"
                                message: "指定ID的文章不存在"

    examples:
        ArticleCreateExample:
            value:
                title: "OpenAPI规范指南"
                content: "本文详细介绍了...",
                tags: [ "api", "documentation" ]
```

遵循这些规范可以确保您的 OpenAPI 文档：

- 结构清晰
- 易于维护
- 工具友好
- 便于团队协作
