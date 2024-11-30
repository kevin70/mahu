-- liquibase formatted sql


-- changeset kzou227@qq.com:202403202333
create table system.role
(
    id          integer primary key not null, -- 主键
    create_time timestamp without time zone,  -- 创建时间
    update_time timestamp without time zone,  -- 更新时间
    deleted     "char",                       -- 软删除标识
    name        character varying(32),        -- 角色名称
    remark      character varying(4000),      -- 备注
    ordering    smallint,                     -- 排序值
    permits     character varying[]           -- 拥有的权限代码
);
comment on table system.role is '权限表';
comment on column system.role.id is '主键';
comment on column system.role.create_time is '创建时间';
comment on column system.role.update_time is '更新时间';
comment on column system.role.deleted is '软删除标识';
comment on column system.role.name is '角色名称';
comment on column system.role.remark is '备注';
comment on column system.role.ordering is '排序值';
comment on column system.role.permits is '拥有的权限代码';

create unique index roles_name_key on system.role using btree (name);
-- rollback drop table system.role;


-- changeset kzou227@qq.com:202403211352
INSERT INTO system.role (id, create_time, update_time, deleted, name, remark,
                         ordering, permits)
VALUES (1, current_timestamp, current_timestamp, 'F', '超级管理员',
        '系统超级管理员，拥有系统所有权限', 9999, '{*}');
-- rollback delete from system.role where id=1;
