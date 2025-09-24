--
-- liquibase formatted sql
-- changeset kzou227@qq.com:202508251124
CREATE TABLE
  sys.role (
    id INTEGER NOT NULL CONSTRAINT role_pk PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted "char" DEFAULT 'F'::"char",
    NAME VARCHAR(32),
    remark VARCHAR(255),
    ordering SMALLINT,
    permissions jsonb
  );

COMMENT ON TABLE sys.role IS '权限表';

COMMENT ON COLUMN sys.role.id IS '主键';

COMMENT ON COLUMN sys.role.created_at IS '创建时间';

COMMENT ON COLUMN sys.role.updated_at IS '更新时间';

COMMENT ON COLUMN sys.role.deleted IS '软删除标识';

COMMENT ON COLUMN sys.role.name IS '角色名称';

COMMENT ON COLUMN sys.role.remark IS '备注';

COMMENT ON COLUMN sys.role.ordering IS '排序值';

COMMENT ON COLUMN sys.role.permissions IS '拥有的权限代码';

CREATE INDEX role_name_ui ON sys.role (NAME)
WHERE
  (deleted = 'F'::"char");

-- rollback drop table sys.role;
