-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-005
CREATE TABLE sys.auth_clients (
  client_id VARCHAR(50) NOT NULL CONSTRAINT auth_clients_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  deleted BOOLEAN DEFAULT FALSE,
  client_secret VARCHAR(2048),
  remark VARCHAR(512),
  label VARCHAR(128),
  terminal_type VARCHAR(32),
  wechat_appid VARCHAR(128),
  wechat_appsecret VARCHAR(2048)
);

COMMENT ON TABLE sys.auth_clients IS '认证客户端';

-- rollback drop table sys.auth_clients;
