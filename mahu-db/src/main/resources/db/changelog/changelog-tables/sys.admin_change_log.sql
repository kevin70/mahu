-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202601311929
CREATE TABLE sys.admin_change_log (
  id VARCHAR(50) NOT NULL CONSTRAINT admin_change_log_pk PRIMARY KEY,
  created_at TIMESTAMP,
  admin_id INTEGER,
  ip_addr VARCHAR(32),
  source VARCHAR(64)
);

comment ON TABLE sys.admin_change_log IS '管理员变更记录';

comment ON COLUMN sys.admin_change_log.id IS '主键';

comment ON COLUMN sys.admin_change_log.created_at IS '创建时间';

comment ON COLUMN sys.admin_change_log.admin_id IS '管理员 ID';

comment ON COLUMN sys.admin_change_log.ip_addr IS '操作 IP';

comment ON COLUMN sys.admin_change_log.source IS '操作来源';

-- rollback DROP TABLE sys.admin_change_log;
;

-- changeset kzou227@qq.com:202601311930
CREATE TABLE sys.admin_change_item (
  id VARCHAR(50) NOT NULL CONSTRAINT admin_change_item_pk PRIMARY KEY,
  created_at TIMESTAMP,
  change_log_id VARCHAR(50),
  type VARCHAR(128),
  tenant_id VARCHAR(32),
  data_id VARCHAR(128),
  event_time BIGINT,
  data JSONB,
  old_data JSONB
);

comment ON TABLE sys.admin_change_item IS '管理员变更项';

comment ON COLUMN sys.admin_change_item.id IS '主键';

comment ON COLUMN sys.admin_change_item.created_at IS '创建时间';

comment ON COLUMN sys.admin_change_item.change_log_id IS '变更记录 ID';

comment ON COLUMN sys.admin_change_item.type IS '修改类型';

comment ON COLUMN sys.admin_change_item.tenant_id IS '租户 ID';

comment ON COLUMN sys.admin_change_item.data_id IS '数据 ID';

comment ON COLUMN sys.admin_change_item.event_time IS '事件创建时间戳';

comment ON COLUMN sys.admin_change_item.data IS '新数据';

comment ON COLUMN sys.admin_change_item.old_data IS '旧数据';

CREATE INDEX admin_change_item_change_log_id_idx ON sys.admin_change_item (change_log_id);

-- rollback DROP TABLE sys.admin_change_item;
