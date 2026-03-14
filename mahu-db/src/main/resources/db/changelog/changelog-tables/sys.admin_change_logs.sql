-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202601311929
CREATE TABLE sys.admin_change_logs (
  id VARCHAR(50) NOT NULL CONSTRAINT admin_change_logs_pk PRIMARY KEY,
  created_at TIMESTAMP,
  admin_id INTEGER,
  ip_addr VARCHAR(32),
  source VARCHAR(64)
);

COMMENT ON TABLE sys.admin_change_logs IS '管理员变更记录';

COMMENT ON COLUMN sys.admin_change_logs.id IS '主键';

COMMENT ON COLUMN sys.admin_change_logs.created_at IS '创建时间';

COMMENT ON COLUMN sys.admin_change_logs.admin_id IS '管理员 ID';

COMMENT ON COLUMN sys.admin_change_logs.ip_addr IS '操作 IP';

COMMENT ON COLUMN sys.admin_change_logs.source IS '操作来源';

-- rollback DROP TABLE sys.admin_change_logs;
