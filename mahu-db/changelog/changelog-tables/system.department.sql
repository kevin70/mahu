-- liquibase formatted sql


-- changeset kzou227@qq.com:202403202337
create table system.department
(
    id          serial primary key,          -- 主键
    create_time timestamp without time zone, -- 创建时间
    update_time timestamp without time zone, -- 更新时间
    name        character varying(32),       -- 名称
    parent_id   integer,                     -- 父组织 ID
    leader_id   integer,                     -- 负责人 ID
    ordering    integer                      -- 排序值
);
comment on table system.department is '部门';
comment on column system.department.id is '主键';
comment on column system.department.create_time is '创建时间';
comment on column system.department.update_time is '更新时间';
comment on column system.department.name is '名称';
comment on column system.department.parent_id is '父组织 ID';
comment on column system.department.leader_id is '负责人 ID';
comment on column system.department.ordering is '排序值';

alter sequence system.department_id_seq restart with 30001;
-- rollback drop table system.department;
