-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251101
CREATE TABLE sys.roles (
  id INTEGER NOT NULL CONSTRAINT roles_pk PRIMARY KEY,
  created_at TIMESTAMP WITHOUT TIME ZONE,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  deleted BOOLEAN DEFAULT FALSE,
  name VARCHAR(32),
  remark VARCHAR(255),
  ordering SMALLINT,
  permissions JSONB
);

COMMENT ON TABLE sys.roles IS '权限表';

COMMENT ON COLUMN sys.roles.id IS '主键';

COMMENT ON COLUMN sys.roles.created_at IS '创建时间';

COMMENT ON COLUMN sys.roles.updated_at IS '更新时间';

COMMENT ON COLUMN sys.roles.deleted IS '软删除标识';

COMMENT ON COLUMN sys.roles.name IS '角色名称';

COMMENT ON COLUMN sys.roles.remark IS '备注';

COMMENT ON COLUMN sys.roles.ordering IS '排序值';

COMMENT ON COLUMN sys.roles.permissions IS '拥有的权限代码';

CREATE INDEX roles_name_idx ON sys.roles (NAME)
WHERE
  (deleted = FALSE);

-- rollback drop table sys.roles;
