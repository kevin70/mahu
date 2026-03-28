-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-010
CREATE TABLE sys.admin_roles (
  admin_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL
);

CREATE UNIQUE INDEX admin_roles_admin_id_role_id_uidx ON sys.admin_roles (admin_id, role_id);

-- rollback drop table sys.admin_roles;
