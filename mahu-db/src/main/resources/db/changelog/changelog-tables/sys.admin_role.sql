-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251133
create table sys.admin_role
(
    admin_id integer not null,
    role_id  integer not null
);

comment
on column sys.admin_role.admin_id is '管理员 ID';

comment
on column sys.admin_role.role_id is '角色ID';

create unique index admin_role_admin_id_role_id_ui on sys.admin_role (admin_id, role_id);
-- rollback drop table sys.admin_role;
