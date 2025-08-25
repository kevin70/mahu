-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251133
create table system.admin_role
(
    admin_id integer not null,
    role_id  integer not null
);

comment
on column system.admin_role.admin_id is '管理员 ID';

comment
on column system.admin_role.role_id is '角色ID';

create unique index admin_role_admin_id_role_id_ui on system.admin_role (admin_id, role_id);
-- rollback drop table system.admin_role;
