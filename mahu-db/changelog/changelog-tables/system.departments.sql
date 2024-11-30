-- liquibase formatted sql


-- changeset kzou227@qq.com:202403202337
create table system.departments
(
    id          serial
        primary key,
    create_time timestamp,
    update_time timestamp,
    name        varchar(32),
    parent_id   integer,
    leader_id   integer,
    ordering    integer
);

comment
on table system.departments is '部门';

comment
on column system.departments.id is '主键';

comment
on column system.departments.create_time is '创建时间';

comment
on column system.departments.update_time is '更新时间';

comment
on column system.departments.name is '名称';

comment
on column system.departments.parent_id is '父组织 ID';

comment
on column system.departments.leader_id is '负责人 ID';

comment
on column system.departments.ordering is '排序值';

alter sequence system.departments_id_seq restart with 30001;
-- rollback drop table system.departments;
