---
name: liquibase-sql
description: Best practices for writing Liquibase SQL changesets, including changeset ID naming, file naming, execution order, runOnChange usage, rollback strategy, and team collaboration conventions. Use this skill whenever the user asks about Liquibase, database migration, changeset authoring, SQL changelog structure, or any question about managing DB schema changes with Liquibase — even if they just say "how do I write a changeset" or "help me organize my migrations."
---

# Liquibase SQL Changeset Best Practices

## 执行顺序

Liquibase 严格按 changelog 定义的顺序执行，不按文件名或时间戳自动排序。

| 场景 | 执行顺序依据 |
|------|------------|
| 单文件内多个 changeset | 文件中从上到下的顺序 |
| `<include>` 多文件 | changelog 中 include 的书写顺序 |
| `<includeAll>` 目录 | 文件名的字母/数字升序 |
| 已执行过的 changeset | 跳过（通过 DATABASECHANGELOG 表判断） |

唯一键组成：`id + author + filepath`，三者共同标识一条 changeset 记录。

---

## 文件命名规则

### 推荐格式

```
V{日期或版本}_{序号}__{描述}.sql
```

### 两种主流风格

**按日期（持续迭代项目）：**
```
V20240101_001__create_users_table.sql
V20240101_002__create_orders_table.sql
V20240215_001__add_user_status.sql
```

**按版本号（版本发布制项目）：**
```
V1.0.0_001__create_users_table.sql
V1.1.0_001__add_user_status.sql
V1.2.0_001__add_payment_index.sql
```

### 描述部分规范

用下划线分隔单词，动词开头：

```
✓ __create_users_table
✓ __add_payment_status_to_orders
✓ __create_index_orders_user_id
✓ __drop_legacy_temp_table

✗ __fix           ← 太模糊
✗ __update        ← 不知道改了什么
✗ __addColumn     ← 驼峰不统一
```

### 序号必须用前导零

```
✓ _001_  _002_  _010_  _099_  _100_
✗ _1_    _2_    _10_    ← 字母排序时 _10_ 排在 _2_ 前面
```

### 文件命名禁止事项

| 禁止 | 原因 |
|------|------|
| 重命名已执行的文件 | filepath 是唯一键的一部分，改名后视为新文件 |
| 文件名含空格 | CLI 和部分系统解析异常 |
| 中文或特殊字符 | 跨平台兼容性差 |
| 不加序号直接用日期 | 同一天多个文件排序不可控 |

---

## Changeset ID 命名规则

### 推荐格式

```
日期 + 序号
```

```sql
-- changeset dev:20240301-001
-- changeset dev:20240301-002
```

### 团队协作防冲突

多人同天开发时，利用不同 author 区分，ID 可以相同：

```sql
-- changeset alice:20240301-001
-- changeset bob:20240301-001     ← author 不同，不冲突
```

### ID 命名禁止事项

| 禁止 | 原因 |
|------|------|
| 修改已执行的 changeset ID | DATABASECHANGELOG 找不到记录，视为新 changeset 重复执行 |
| 同文件内重复 ID | Liquibase 启动报错 |
| 使用空格或特殊字符 | 部分数据库驱动解析异常 |
| 过于简短如 `1`、`2` | 多文件时极易冲突，可读性差 |

---

## SQL 文件结构最佳实践

### 文件头部规范

```sql
-- liquibase formatted sql

-- changeset dev:20240301-001 labels:v1.0 context:prod,staging
-- comment: 创建用户主表
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_name='users'
CREATE TABLE users (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    status      VARCHAR(20)     NOT NULL DEFAULT 'active',
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);
-- rollback DROP TABLE IF EXISTS users;
```

关键注释字段：

| 字段 | 作用 |
|------|------|
| `labels` | 按标签筛选执行，适合多版本管理 |
| `context` | 按环境执行，如 `prod` / `dev` / `test` |
| `comment` | 描述变更意图，写入 DATABASECHANGELOG |
| `preconditions` | 执行前检查，防止重复或错误执行 |
| `rollback` | 定义回滚 SQL |

### 各类操作写法

**DDL：建表 / 加字段**
```sql
-- changeset dev:20240201-001
-- comment: 订单表新增支付状态字段
ALTER TABLE orders ADD COLUMN payment_status VARCHAR(20) NOT NULL DEFAULT 'pending';
-- rollback ALTER TABLE orders DROP COLUMN payment_status;
```

**DML：数据迁移**
```sql
-- changeset dev:20240201-002 runInTransaction:true
-- comment: 迁移旧状态值到新枚举
UPDATE orders SET payment_status = 'paid'   WHERE old_status = 1;
UPDATE orders SET payment_status = 'failed' WHERE old_status = 2;
-- rollback UPDATE orders SET payment_status = 'pending' WHERE old_status IN (1,2);
```

> 大批量数据迁移时加 `runInTransaction:false` + 分批处理，避免锁表。

**视图 / 函数（runOnChange）**
```sql
-- changeset dev:view-v_order_summary runOnChange:true
-- comment: 订单汇总视图
CREATE OR REPLACE VIEW v_order_summary AS
SELECT
    o.id,
    o.user_id,
    u.name         AS user_name,
    o.total_amount,
    o.status,
    o.created_at
FROM orders o
JOIN users u ON u.id = o.user_id;
-- rollback DROP VIEW IF EXISTS v_order_summary;
```

**仅开发/测试环境的种子数据**
```sql
-- changeset dev:20240201-003 context:dev,test
-- comment: 插入测试种子数据
INSERT INTO users (name, email) VALUES ('测试用户', 'test@example.com');
-- rollback DELETE FROM users WHERE email = 'test@example.com';
```

---

## runOnChange 使用规范

### 工作原理

```
首次执行  → 执行 SQL → 计算 MD5 checksum → 写入 DATABASECHANGELOG
再次执行（内容未变）→ checksum 一致 → 跳过
再次执行（内容有改动）→ checksum 不一致 → 重新执行
```

### 适用场景

| 适合 | 不适合 |
|------|--------|
| 视图 View | CREATE TABLE |
| 存储过程 / 函数 | ALTER TABLE |
| 触发器 Trigger | 数据迁移 DML |
| Oracle Package | 索引创建 |

### SQL 必须是幂等的

```sql
-- ✓ 视图用 CREATE OR REPLACE
CREATE OR REPLACE VIEW v_active_users AS ...;

-- ✓ 函数用 CREATE OR REPLACE FUNCTION
CREATE OR REPLACE FUNCTION fn_calc_tax(price DECIMAL) ...;

-- ✓ 触发器需先 DROP（不支持 OR REPLACE）
DROP TRIGGER IF EXISTS trg_orders_update ON orders;
CREATE TRIGGER trg_orders_update ...;
```

### runOnChange vs runAlways

| 属性 | 触发条件 | 适用场景 |
|------|---------|---------|
| `runOnChange:true` | 内容有变化时才重新执行 | 视图、函数、存储过程 |
| `runAlways:true` | 每次 liquibase 运行都执行 | 数据同步、统计刷新 |

> ⚠️ 空白和注释的变化也会触发 runOnChange，保持文件格式统一。

---

## 目录结构

```
db/
├── changelog-root.yaml
├── migrations/               ← DDL 结构变更
│   ├── V20240101_001__create_users_table.sql
│   ├── V20240101_002__create_orders_table.sql
│   └── V20240215_001__add_user_status.sql
├── data/                     ← 生产基础数据（角色、字典、配置等）
│   ├── V20240101_001__init_roles.sql
│   └── V20240215_001__add_new_region.sql
├── views/                    ← runOnChange，无序号
│   ├── v_order_summary.sql
│   └── v_active_users.sql
├── functions/
│   └── fn_calc_tax.sql
└── seeds/                    ← 仅 context=dev,test 的测试假数据
    └── V20240101_001__seed_test_users.sql
```

**changelog-root.yaml：**
```yaml
databaseChangeLog:
  - includeAll:
      path: db/migrations/
      relativeToChangelogFile: true
  - includeAll:
      path: db/data/
      relativeToChangelogFile: true
  - includeAll:
      path: db/views/
      relativeToChangelogFile: true
  - includeAll:
      path: db/functions/
      relativeToChangelogFile: true
```

---

## 基础数据写法（data/ 目录）

**首次插入：**

```sql
-- liquibase formatted sql

-- changeset dev:20240101-001 context:prod,staging
-- comment: 初始化系统角色
INSERT INTO roles (code, name) VALUES
    ('ADMIN',    '系统管理员'),
    ('OPERATOR', '操作员'),
    ('VIEWER',   '只读用户');

-- rollback DELETE FROM roles WHERE code IN ('ADMIN','OPERATOR','VIEWER');
```

**后续更新已有数据，新增 changeset，不修改原来的：**

```sql
-- changeset dev:20240215-001 context:prod,staging
-- comment: 更新操作员角色名称
UPDATE roles SET name = '业务操作员' WHERE code = 'OPERATOR';

-- rollback UPDATE roles SET name = '操作员' WHERE code = 'OPERATOR';
```

| 场景 | 做法 |
|------|------|
| 新增基础数据 | 新文件新 changeset，直接 INSERT |
| 修改已有基础数据 | 新增 changeset，不修改原来的 |
| 删除基础数据 | 新增 changeset，写好 rollback |
| 大量初始数据 | 按业务模块拆分文件，每个文件一个主题 |

---

## Preconditions 防护

```sql
-- 建表前检查表是否存在
-- preconditions onFail:MARK_RAN onError:HALT
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_name='users'

-- 加字段前检查字段是否存在
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name='orders' AND column_name='payment_status'
```

| `onFail` 值 | 行为 |
|------------|------|
| `HALT` | 停止执行，报错（默认） |
| `MARK_RAN` | 标记为已执行，跳过 |
| `CONTINUE` | 跳过此 changeset，继续 |
| `WARN` | 打印警告，继续执行 |

---

## 回滚策略

每个 changeset 都必须写 rollback：

```sql
-- changeset dev:20240301-001
CREATE INDEX idx_orders_user_id ON orders(user_id);
-- rollback DROP INDEX IF EXISTS idx_orders_user_id;
```

常用回滚命令：

```bash
liquibase rollbackCount 1               # 回滚最近 1 个
liquibase rollback v1.2.0               # 回滚到指定 tag
liquibase rollbackToDate 2024-03-01     # 回滚到指定日期
```

---

## 环境隔离

```bash
liquibase --contexts=prod update
liquibase --contexts="prod,migration" update
```

---

## 全局禁止事项

| 禁止 | 原因 |
|------|------|
| 修改已执行的 changeset 内容 | checksum 校验失败，部署中断 |
| 多个变更写在一个 changeset | 回滚粒度太粗，难以排查 |
| 不写 rollback | 生产事故无法快速回退 |
| `runAlways:true` 用于 DDL | 每次部署都执行，极易出错 |
| 重命名已执行的文件或修改 ID | 视为新记录，导致重复执行 |

---

## 核心原则一句话

> **一个 changeset 只做一件事，写好 rollback，用 context 隔离环境，视图/函数用 runOnChange，普通 DDL/DML 永远不改只新增，文件和 ID 一旦执行永远不改名。**
