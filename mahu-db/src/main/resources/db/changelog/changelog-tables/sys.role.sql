-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251124
create table sys.role
(
    id          integer not null
        constraint role_pk
            primary key,
    created_at  timestamp without time zone,
    updated_at  timestamp without time zone,
    deleted     "char" default 'F'::"char",
    name        varchar(32),
    remark      varchar(255),
    ordering    smallint,
    permissions jsonb
);

comment
on table sys.role is '权限表';

comment
on column sys.role.id is '主键';

comment
on column sys.role.created_at is '创建时间';

comment
on column sys.role.updated_at is '更新时间';

comment
on column sys.role.deleted is '软删除标识';

comment
on column sys.role.name is '角色名称';

comment
on column sys.role.remark is '备注';

comment
on column sys.role.ordering is '排序值';

comment
on column sys.role.permissions is '拥有的权限代码';

create index role_name_ui
    on sys.role (name) where (deleted = 'F'::"char");
-- rollback drop table sys.role;
