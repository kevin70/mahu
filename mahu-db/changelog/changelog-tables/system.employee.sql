-- liquibase formatted sql


-- changeset kzou227@qq.com:202403202347
create table system.employee
(
    id            bigserial primary key,       -- 主键
    create_time   timestamp without time zone, -- 创建时间
    update_time   timestamp without time zone, -- 更新时间
    deleted       "char",                      -- 软删除标志
    username      character varying(128),      -- 用户名
    password      character varying(4000),     -- 登录密码
    nickname      character varying(128),      -- 昵称
    avatar        character varying(128),      -- 头像地址
    status        smallint,                    -- 用户状态
-- - 1：普通
-- - 2：冻结
    department_id integer                      -- 用户部门 ID
);
comment on table system.employee is '员工';
comment on column system.employee.id is '主键';
comment on column system.employee.create_time is '创建时间';
comment on column system.employee.update_time is '更新时间';
comment on column system.employee.deleted is '软删除标志';
comment on column system.employee.username is '用户名';
comment on column system.employee.password is '登录密码';
comment on column system.employee.nickname is '昵称';
comment on column system.employee.avatar is '头像地址';
comment on column system.employee.status is '用户状态
- 1：普通
- 2：冻结';
comment on column system.employee.department_id is '用户部门 ID';

create unique index employees_username_key on system.employee using btree (username);

alter sequence system.employee_id_seq restart with 30001;
-- rollback drop table system.employee;


-- changeset kzou227@qq.com:202403211355
INSERT INTO system.employee (id, create_time, update_time, deleted, username,
                             password, nickname, avatar, status)
VALUES (1, '2023-11-08 18:15:28.729964', '2024-01-24 16:28:53.806000', 'F', 'houge',
        '$argon2id$v=19$m=15360,t=2,p=1$RmT8NvroGRJkFVnmimIxcOM3foB5lPOFQYj5OHhyYSFJf+YByD9nAiIZXOZFiVaLb5YPve4v4lfyJyyuE6+sBg$JCSB3sXOeemq5m6JQ1Uo56+GIwlOvotRR7npwUMznns',
        '超级管理员',
        'https://houge-2023.oss-cn-hangzhou.aliyuncs.com/admin-avatar/20240124/95536b72bebccad5fd28ef25a818e76e.jpg',
        1);
-- rollback delete from system.employee where id=1;
