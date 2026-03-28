-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-011
CREATE TABLE sys.admin_access_logs (
  id UUID NOT NULL CONSTRAINT admin_access_logs_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  admin_id INTEGER,
  ip_addr VARCHAR(64),
  method VARCHAR(16),
  uri_path VARCHAR(1024),
  uri_query VARCHAR(1024),
  referer VARCHAR(2048),
  protocol VARCHAR(1024),
  response_status SMALLINT,
  response_bytes BIGINT,
  user_agent VARCHAR(2048)
);

CREATE INDEX admin_access_logs_admin_id_idx ON sys.admin_access_logs (admin_id);

-- rollback drop table sys.admin_access_logs;
