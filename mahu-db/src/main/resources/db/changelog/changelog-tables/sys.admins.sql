-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251102
CREATE TABLE sys.admins (
  id serial CONSTRAINT admins_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  deleted BOOLEAN DEFAULT FALSE,
  username VARCHAR(128),
  password VARCHAR(1024),
  nickname VARCHAR(128),
  avatar VARCHAR(128),
  status SMALLINT,
  gender SMALLINT
);

COMMENT ON TABLE sys.admins IS '员工';

COMMENT ON COLUMN sys.admins.id IS '主键';

COMMENT ON COLUMN sys.admins.created_at IS '创建时间';

COMMENT ON COLUMN sys.admins.updated_at IS '更新时间';

COMMENT ON COLUMN sys.admins.deleted IS '软删除标志';

COMMENT ON COLUMN sys.admins.username IS '用户名';

COMMENT ON COLUMN sys.admins.password IS '登录密码';

COMMENT ON COLUMN sys.admins.nickname IS '昵称';

COMMENT ON COLUMN sys.admins.avatar IS '头像地址';

COMMENT ON COLUMN sys.admins.status IS '用户状态';

COMMENT ON COLUMN sys.admins.gender IS '性别
- 0：其他
- 1：男性
- 2：女性';

CREATE UNIQUE INDEX admins_username_uidx ON sys.admins (username)
WHERE
  (deleted = FALSE);

ALTER SEQUENCE sys.admins_id_seq RESTART
WITH
  100001;

-- rollback drop table sys.admins;
