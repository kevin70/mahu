-- liquibase formatted sql


-- changeset kzou227@qq.com:202403202347
create table system.employees
(
    id            serial
        primary key,
    create_time   timestamp,
    update_time   timestamp,
    deleted       "char",
    username      varchar(128)
        unique,
    password      varchar(1024),
    nickname      varchar(128),
    avatar        varchar(128),
    status        smallint,
    department_id integer
);

comment
on table system.employees is '员工';

comment
on column system.employees.id is '主键';

comment
on column system.employees.create_time is '创建时间';

comment
on column system.employees.update_time is '更新时间';

comment
on column system.employees.deleted is '软删除标志';

comment
on column system.employees.username is '用户名';

comment
on column system.employees.password is '登录密码';

comment
on column system.employees.nickname is '昵称';

comment
on column system.employees.avatar is '头像地址';

comment
on column system.employees.status is '用户状态
- 1：普通
- 2：冻结';

comment
on column system.employees.department_id is '用户部门 ID';

alter sequence system.employees_id_seq restart with 30001;
-- rollback drop table system.employees;


-- changeset kzou227@qq.com:202403211355
INSERT INTO system.employees (id, create_time, update_time, deleted, username,
                              password, nickname, avatar, status)
VALUES (1, '2023-11-08 18:15:28.729964', '2024-01-24 16:28:53.806000', 'F', 'houge',
        '$argon2id$v=19$m=15360,t=2,p=1$RmT8NvroGRJkFVnmimIxcOM3foB5lPOFQYj5OHhyYSFJf+YByD9nAiIZXOZFiVaLb5YPve4v4lfyJyyuE6+sBg$JCSB3sXOeemq5m6JQ1Uo56+GIwlOvotRR7npwUMznns',
        '超级管理员',
        'https://houge-2023.oss-cn-hangzhou.aliyuncs.com/admin-avatar/20240124/95536b72bebccad5fd28ef25a818e76e.jpg',
        1);
-- rollback delete from system.employees where id=1;
