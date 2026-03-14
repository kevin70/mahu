-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202601311930
CREATE TABLE sys.admin_change_items (
  id VARCHAR(50) NOT NULL CONSTRAINT admin_change_items_pk PRIMARY KEY,
  created_at TIMESTAMP,
  change_log_id VARCHAR(50),
  table_name VARCHAR(128),
  tenant_id VARCHAR(32),
  data_id VARCHAR(128),
  change_type VARCHAR(128),
  event_time BIGINT,
  data TEXT,
  old_data TEXT
);

COMMENT ON TABLE sys.admin_change_items IS '管理员变更项';

COMMENT ON COLUMN sys.admin_change_items.id IS '主键';

COMMENT ON COLUMN sys.admin_change_items.created_at IS '创建时间';

COMMENT ON COLUMN sys.admin_change_items.change_log_id IS '变更记录 ID';

COMMENT ON COLUMN sys.admin_change_items.table_name IS '表格';

COMMENT ON COLUMN sys.admin_change_items.tenant_id IS '租户 ID';

COMMENT ON COLUMN sys.admin_change_items.data_id IS '数据 ID';

COMMENT ON COLUMN sys.admin_change_items.change_type IS '变更类型';

COMMENT ON COLUMN sys.admin_change_items.event_time IS '事件创建时间戳';

COMMENT ON COLUMN sys.admin_change_items.data IS '新数据';

COMMENT ON COLUMN sys.admin_change_items.old_data IS '旧数据';

CREATE INDEX admin_change_items_change_log_id_idx ON sys.admin_change_items (change_log_id);

-- rollback DROP TABLE sys.admin_change_items;
