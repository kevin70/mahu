-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260414-001
CREATE TABLE sys.admin_login_attempts (
  id UUID NOT NULL CONSTRAINT admin_login_attempts_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  admin_id INTEGER,
  grant_type VARCHAR(50) NOT NULL,
  client_id VARCHAR(26),
  username VARCHAR(191),
  success BOOLEAN NOT NULL,
  reason_code VARCHAR(50),
  reason_detail VARCHAR(255),
  ip_addr VARCHAR(50),
  user_agent VARCHAR(2048)
);

CREATE INDEX admin_login_attempts_admin_id_idx ON sys.admin_login_attempts (admin_id);
CREATE INDEX admin_login_attempts_client_id_idx ON sys.admin_login_attempts (client_id);
CREATE INDEX admin_login_attempts_ip_addr_created_at_idx ON sys.admin_login_attempts (ip_addr, created_at DESC);
CREATE INDEX admin_login_attempts_username_created_at_idx ON sys.admin_login_attempts (username, created_at DESC);
CREATE INDEX admin_login_attempts_success_created_at_idx ON sys.admin_login_attempts (success, created_at DESC);

-- rollback drop table sys.admin_login_attempts;
