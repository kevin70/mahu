-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-008
CREATE TABLE sys.roles (
  id INTEGER NOT NULL CONSTRAINT roles_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  deleted BOOLEAN DEFAULT FALSE,
  name VARCHAR(32),
  remark VARCHAR(255),
  ordering SMALLINT,
  permissions JSONB
);

CREATE INDEX roles_name_idx ON sys.roles (NAME)
WHERE
  (deleted = FALSE);

-- rollback drop table sys.roles;
