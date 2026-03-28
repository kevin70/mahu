-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-014
CREATE TABLE sys.admin_change_items (
  id UUID NOT NULL CONSTRAINT admin_change_items_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  change_log_id UUID,
  table_name VARCHAR(128),
  tenant_id VARCHAR(32),
  data_id VARCHAR(128),
  change_type VARCHAR(128),
  event_time BIGINT,
  data TEXT,
  old_data TEXT
);

CREATE INDEX admin_change_items_change_log_id_idx ON sys.admin_change_items (change_log_id);

-- rollback DROP TABLE sys.admin_change_items;
