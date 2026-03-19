---
name: mahu-ebean-itest
description: Standardizes Mahu integration tests using Testcontainers Postgres + Liquibase schema migration + EBean. Use when creating/updating tests under mahu-domain (repository/service) that need a real database, when the user mentions PostgresLiquibaseTestBase, DictGroupRepositoryTest, Testcontainers, Liquibase, or EBean, or when asked to avoid raw SQL in tests.
---

# Mahu EBean 集成测试（Testcontainers + Liquibase）

## 适用场景

- 在 `mahu-domain` 写/改 **repository/service 的集成测试**，需要真实 PostgreSQL 行为（类型、索引、SQL 语义）
- 希望测试数据库 **自动迁移 schema**（与 `mahu-db` 的 Liquibase 脚本一致）
- 需要 **每个用例隔离**（事务回滚），并避免手写 SQL

## 默认约定（Mahu）

- **数据库**：使用 `PostgresLiquibaseTestBase`（Testcontainers Postgres + Liquibase）
- **迁移入口**：`db/changelog/changelog-root.yaml`
- **隔离方式**：每个 `@Test` 用例开启事务并回滚（由基类实现）
- **写数据**：优先 `db().save(...)` / `db().saveAll(...)`
- **更新数据**：优先 EBean 的更新 API（见下文），不要直接 `sqlUpdate("UPDATE ...")`

## 推荐写法（步骤）

1. 让测试类 `extends PostgresLiquibaseTestBase`
2. 通过 `db()` 获取 `io.ebean.Database`
3. 构造测试数据：
   - 使用 Instancio（如已有 model），或手动 new entity
   - 显式设置业务主键（如 `DictGroup.id`）以便断言
4. 需要“可控的更新时间/排序时间”时：
   - 不要直接写 SQL
   - 不要用实体 `update()` 去“强行指定” `updatedAt`（若字段上有 `@WhenModified`，可能会被覆盖）
   - 使用 **EBean UpdateQuery**：`db().update(Entity.class).set("field", value).where()...update()`
5. 断言结果时尽量只断言业务相关字段，避免时间戳等随机字段影响稳定性

## 模板：避免 SQL 的更新时间控制（推荐）

把“造排序数据”的更新逻辑写成测试内部的小方法，示例：

```java
private void setUpdatedAt(String groupId, Instant updatedAt) {
  db().update(DictGroup.class)
      .set("updatedAt", updatedAt)
      .where()
      .idEq(groupId)
      .update();
}
```

然后在用例里直接调用（3 行即可，避免为了少写两行引入 Map/循环噪音）：

```java
setUpdatedAt("g1", Instant.parse("2020-01-01T00:00:00Z"));
setUpdatedAt("g2", Instant.parse("2020-01-02T00:00:00Z"));
setUpdatedAt("g3", Instant.parse("2020-01-03T00:00:00Z"));
```

## 常见坑与处理

- **`@WhenModified` 覆盖更新时间**：
  - 现象：你 set 了 `updatedAt`，但 update 时被自动改成“现在”
  - 处理：用 UpdateQuery `set("updatedAt", ...)`（如上模板），而不是实体 `update()`
- **数据与线上冲突的担忧**：
  - 默认不会：测试使用容器数据库（独立 JDBC URL）
  - 若担心误配：在基类增加 fail-fast 校验（例如校验 DB 名必须为 `mahu_test`）

## 参考（仓库内样例）

- `mahu-domain/src/test/java/cool/houge/mahu/testing/PostgresLiquibaseTestBase.java`
- `mahu-domain/src/test/java/cool/houge/mahu/repository/DictGroupRepositoryTest.java`

