--
-- liquibase formatted sql
-- changeset kzou227@qq.com:202508251122
CREATE TABLE
  sys.admin (
    id serial CONSTRAINT admin_pk PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted "char" DEFAULT 'F'::"char",
    username VARCHAR(128),
    PASSWORD VARCHAR(1024),
    nickname VARCHAR(128),
    avatar VARCHAR(128),
    status SMALLINT,
    gender SMALLINT
  );

COMMENT ON TABLE sys.admin IS '员工';

COMMENT ON COLUMN sys.admin.id IS '主键';

COMMENT ON COLUMN sys.admin.created_at IS '创建时间';

COMMENT ON COLUMN sys.admin.updated_at IS '更新时间';

COMMENT ON COLUMN sys.admin.deleted IS '软删除标志';

COMMENT ON COLUMN sys.admin.username IS '用户名';

COMMENT ON COLUMN sys.admin.password IS '登录密码';

COMMENT ON COLUMN sys.admin.nickname IS '昵称';

COMMENT ON COLUMN sys.admin.avatar IS '头像地址';

COMMENT ON COLUMN sys.admin.status IS '用户状态';

COMMENT ON COLUMN sys.admin.gender IS '性别
- 0：其他
- 1：男性
- 2：女性';

CREATE UNIQUE INDEX admin_username_ui ON sys.admin (username)
WHERE
  (deleted = 'F'::"char");

ALTER SEQUENCE sys.admin_id_seq RESTART
WITH
  1001;

-- rollback drop table sys.admin;
