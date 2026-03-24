-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-013
CREATE TABLE sys.admin_change_logs (
  id VARCHAR(50) NOT NULL CONSTRAINT admin_change_logs_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  admin_id INTEGER,
  ip_addr VARCHAR(32),
  source VARCHAR(64)
);

-- rollback DROP TABLE sys.admin_change_logs;
