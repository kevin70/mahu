-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251122
create table system.admin
(
    id         serial
        constraint admin_pk
            primary key,
    created_at timestamp,
    updated_at timestamp,
    deleted    "char" default 'F'::"char",
    username   varchar(128),
    password   varchar(1024),
    nickname   varchar(128),
    avatar     varchar(128),
    status     smallint,
    gender     smallint
);

comment
on table system.admin is '员工';

comment
on column system.admin.id is '主键';

comment
on column system.admin.created_at is '创建时间';

comment
on column system.admin.updated_at is '更新时间';

comment
on column system.admin.deleted is '软删除标志';

comment
on column system.admin.username is '用户名';

comment
on column system.admin.password is '登录密码';

comment
on column system.admin.nickname is '昵称';

comment
on column system.admin.avatar is '头像地址';

comment
on column system.admin.status is '用户状态';

comment
on column system.admin.gender is '性别
- 0：其他
- 1：男性
- 2：女性';

create unique index admin_username_ui
    on system.admin (username) where (deleted = 'F'::"char");

alter sequence system.admin_id_seq restart with 1001;
-- rollback drop table system.admin;
