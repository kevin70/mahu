--
-- liquibase formatted sql
-- changeset kzou227@qq.com:202508251133
CREATE TABLE sys.admin_role (
  admin_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL
);

COMMENT ON COLUMN sys.admin_role.admin_id IS '管理员 ID';

COMMENT ON COLUMN sys.admin_role.role_id IS '角色ID';

CREATE UNIQUE INDEX admin_role_admin_id_role_id_ui ON sys.admin_role (admin_id, role_id);

-- rollback drop table sys.admin_role;
