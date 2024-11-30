# 数据库管理

使用[Liquibase](https://www.liquibase.org/)作为数据库Schema版本管理工具。

:使用原则:

- 数据库Schema更改按版本号存放于`changelogs/[version].sql`文件中
- Liquibase每一个`changeSet`都需要编写对应的`rollback`回滚语句

:示例:

```sql
-- changeset kzou227@qq.com:202304061134
create table person(
  firstname    varchar(255)  not null,
  lastname     varchar(255)  not null
);
-- rollback drop table person;

```

```
-- changeset [author]:[id]
-- rollback [rollback sql];
```
