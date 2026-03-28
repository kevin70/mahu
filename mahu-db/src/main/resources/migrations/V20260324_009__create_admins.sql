-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-009
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

CREATE UNIQUE INDEX admins_username_uidx ON sys.admins (username)
WHERE
  (deleted = FALSE);

ALTER SEQUENCE sys.admins_id_seq RESTART
WITH
  100001;

-- rollback drop table sys.admins;
