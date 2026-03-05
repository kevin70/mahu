-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202601311930
CREATE TABLE sys.admin_change_items (
  id VARCHAR(50) NOT NULL CONSTRAINT admin_change_items_pk PRIMARY KEY,
  created_at TIMESTAMP,
  change_log_id VARCHAR(50),
  type VARCHAR(128),
  tenant_id VARCHAR(32),
  data_id VARCHAR(128),
  event_time BIGINT,
  data JSONB,
  old_data JSONB
);

comment ON TABLE sys.admin_change_items IS '管理员变更项';

comment ON COLUMN sys.admin_change_items.id IS '主键';

comment ON COLUMN sys.admin_change_items.created_at IS '创建时间';

comment ON COLUMN sys.admin_change_items.change_log_id IS '变更记录 ID';

comment ON COLUMN sys.admin_change_items.type IS '修改类型';

comment ON COLUMN sys.admin_change_items.tenant_id IS '租户 ID';

comment ON COLUMN sys.admin_change_items.data_id IS '数据 ID';

comment ON COLUMN sys.admin_change_items.event_time IS '事件创建时间戳';

comment ON COLUMN sys.admin_change_items.data IS '新数据';

comment ON COLUMN sys.admin_change_items.old_data IS '旧数据';

CREATE INDEX admin_change_items_change_log_id_idx ON sys.admin_change_items (change_log_id);

-- rollback DROP TABLE sys.admin_change_items;
