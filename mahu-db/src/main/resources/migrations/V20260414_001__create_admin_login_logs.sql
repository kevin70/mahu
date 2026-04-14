-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260414-001
CREATE TABLE sys.admin_login_logs (
  id UUID NOT NULL CONSTRAINT admin_login_logs_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  admin_id INTEGER,
  grant_type VARCHAR(50) NOT NULL,
  client_id VARCHAR(26),
  username VARCHAR(191),
  success BOOLEAN NOT NULL,
  reason_code VARCHAR(50),
  reason_detail VARCHAR(255),
  ip_addr VARCHAR(50),
  user_agent VARCHAR(512)
);

CREATE INDEX admin_login_logs_admin_id_created_at_idx ON sys.admin_login_logs (admin_id, created_at DESC);
CREATE INDEX admin_login_logs_created_at_idx ON sys.admin_login_logs (created_at DESC);
CREATE INDEX admin_login_logs_username_created_at_idx ON sys.admin_login_logs (username, created_at DESC);

-- rollback drop table sys.admin_login_logs;
