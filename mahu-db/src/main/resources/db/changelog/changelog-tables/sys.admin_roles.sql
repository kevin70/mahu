-- liquibase formatted sql
;

CREATE TABLE sys.admin_roles (
  admin_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL
);

COMMENT ON COLUMN sys.admin_roles.admin_id IS '管理员 ID';

COMMENT ON COLUMN sys.admin_roles.role_id IS '角色ID';

CREATE UNIQUE INDEX admin_role_admin_id_role_id_uidx ON sys.admin_roles (admin_id, role_id);

-- rollback drop table sys.admin_roles;
