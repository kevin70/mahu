-- liquibase formatted sql


-- changeset kzou227@qq.com:202403202333
create table system.roles
(
    id          integer not null
        primary key,
    create_time timestamp,
    update_time timestamp,
    deleted     "char",
    name        varchar(32)
        unique,
    remark      varchar(255),
    ordering    smallint,
    permits     character varying[]
);

comment
on table system.roles is '权限表';

comment
on column system.roles.id is '主键';

comment
on column system.roles.create_time is '创建时间';

comment
on column system.roles.update_time is '更新时间';

comment
on column system.roles.deleted is '软删除标识';

comment
on column system.roles.name is '角色名称';

comment
on column system.roles.remark is '备注';

comment
on column system.roles.ordering is '排序值';

comment
on column system.roles.permits is '拥有的权限代码';
-- rollback drop table system.roles;


-- changeset kzou227@qq.com:202403211352
INSERT INTO system.roles (id, create_time, update_time, deleted, name, remark,
                          ordering, permits)
VALUES (1, current_timestamp, current_timestamp, 'F', '超级管理员',
        '系统超级管理员，拥有系统所有权限', 9999, '{*}');
-- rollback delete from system.roles where id=1000;
