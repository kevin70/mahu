--
-- liquibase formatted sql
-- changeset kzou227@qq.com:202508251146
CREATE TABLE sys.admin_access_log (
  id UUID NOT NULL CONSTRAINT admin_access_log_pk PRIMARY KEY,
  created_at TIMESTAMP,
  admin_id BIGINT,
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

COMMENT ON TABLE sys.admin_access_log IS '管理员访问记录';

COMMENT ON COLUMN sys.admin_access_log.id IS '主键';

COMMENT ON COLUMN sys.admin_access_log.created_at IS '创建时间';

COMMENT ON COLUMN sys.admin_access_log.admin_id IS '管理员 ID';

CREATE INDEX admin_access_log_admin_id_i ON sys.admin_access_log (admin_id);

-- rollback drop table sys.admin_access_log;
