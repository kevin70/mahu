-- liquibase formatted sql


-- changeset kzou227@qq.com:0KPY6J2C5XP5S
create table system.department
(
    id         serial not null
        constraint department_pk
            primary key,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    name       varchar(32),
    parent_id  integer,
    leader_id  integer,
    ordering   integer
);

comment on table system.department is '部门';

comment on column system.department.id is '主键';

comment on column system.department.created_at is '创建时间';

comment on column system.department.updated_at is '更新时间';

comment on column system.department.name is '名称';

comment on column system.department.parent_id is '父组织 ID';

comment on column system.department.leader_id is '负责人 ID';

comment on column system.department.ordering is '排序值';

alter sequence system.department_id_seq restart with 30001;
-- rollback drop table system.department;
