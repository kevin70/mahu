-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251100
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

COMMENT ON COLUMN sys.auth_clients.client_id IS '客户端 ID';

COMMENT ON COLUMN sys.auth_clients.created_at IS '创建时间';

COMMENT ON COLUMN sys.auth_clients.updated_at IS '修改时间';

COMMENT ON COLUMN sys.auth_clients.deleted IS '软删除标识';

COMMENT ON COLUMN sys.auth_clients.client_secret IS '客户端密钥';

COMMENT ON COLUMN sys.auth_clients.remark IS '备注';

COMMENT ON COLUMN sys.auth_clients.label IS '标签';

COMMENT ON COLUMN sys.auth_clients.terminal_type IS '终端类型';

COMMENT ON COLUMN sys.auth_clients.wechat_appid IS '微信应用 ID';

COMMENT ON COLUMN sys.auth_clients.wechat_appsecret IS '微信应用密钥';

-- rollback drop table sys.auth_clients;
