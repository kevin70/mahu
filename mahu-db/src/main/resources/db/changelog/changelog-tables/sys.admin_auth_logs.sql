-- liquibase formatted sql
;

CREATE TABLE sys.admin_auth_logs (
  id UUID NOT NULL CONSTRAINT admin_auth_logs_pk PRIMARY KEY,
  created_at TIMESTAMP,
  admin_id BIGINT,
  grant_type VARCHAR(50),
  client_id VARCHAR(26),
  ip_addr VARCHAR(50),
  user_agent VARCHAR(2048)
);

COMMENT ON TABLE sys.admin_auth_logs IS '管理员认证日志';

COMMENT ON COLUMN sys.admin_auth_logs.id IS '主键';

COMMENT ON COLUMN sys.admin_auth_logs.created_at IS '创建时间';

COMMENT ON COLUMN sys.admin_auth_logs.admin_id IS '管理员用户 ID';

COMMENT ON COLUMN sys.admin_auth_logs.grant_type IS '认证类型
PASSWORD
TOKEN_REFRESH';

COMMENT ON COLUMN sys.admin_auth_logs.client_id IS '认证客户端 ID';

COMMENT ON COLUMN sys.admin_auth_logs.ip_addr IS '认证 IP';

COMMENT ON COLUMN sys.admin_auth_logs.user_agent IS '认证的 User Agent';

CREATE INDEX admin_auth_logs_admin_id_idx ON sys.admin_auth_logs (admin_id);

-- rollback drop table sys.admin_auth_logs;
