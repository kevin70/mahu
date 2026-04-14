-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-012
CREATE TABLE sys.admin_auth_logs (
  id UUID NOT NULL CONSTRAINT admin_auth_logs_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  admin_id BIGINT,
  grant_type VARCHAR(50),
  client_id VARCHAR(26),
  ip_addr VARCHAR(50),
  user_agent VARCHAR(512)
);

CREATE INDEX admin_auth_logs_admin_id_idx ON sys.admin_auth_logs (admin_id);

-- rollback drop table sys.admin_auth_logs;
