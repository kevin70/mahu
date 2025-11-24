-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251147
CREATE TABLE sys.admin_audit_log (
  id UUID NOT NULL CONSTRAINT admin_audit_log_pk PRIMARY KEY,
  created_at TIMESTAMP,
  admin_id BIGINT,
  ip_addr VARCHAR(50),
  change_type "char",
  table_name VARCHAR(50),
  data_tenant_id VARCHAR(50),
  data_id VARCHAR(256),
  data JSONB,
  old_data JSONB
);

COMMENT ON TABLE sys.admin_audit_log IS '管理员操作审计日志';

COMMENT ON COLUMN sys.admin_audit_log.id IS '主键';

COMMENT ON COLUMN sys.admin_audit_log.created_at IS '创建时间';

COMMENT ON COLUMN sys.admin_audit_log.admin_id IS '管理员 ID';

COMMENT ON COLUMN sys.admin_audit_log.ip_addr IS '操作 IP';

COMMENT ON COLUMN sys.admin_audit_log.change_type IS '改变类型';

COMMENT ON COLUMN sys.admin_audit_log.table_name IS '数据库表名';

COMMENT ON COLUMN sys.admin_audit_log.data_tenant_id IS '数据租户 ID';

COMMENT ON COLUMN sys.admin_audit_log.data_id IS '数据主键';

COMMENT ON COLUMN sys.admin_audit_log.data IS '改变的数据';

COMMENT ON COLUMN sys.admin_audit_log.old_data IS '旧的数据';

CREATE INDEX admin_audit_log_admin_id_i ON sys.admin_audit_log (admin_id);

-- rollback drop table sys.admin_audit_log;
