-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-024
CREATE TABLE sys.admin_notification_targets (
  id BIGINT GENERATED ALWAYS AS IDENTITY CONSTRAINT admin_notification_targets_pk PRIMARY KEY,
  notification_id BIGINT NOT NULL,
  admin_id INTEGER NOT NULL,
  created_at TIMESTAMPTZ,
  CONSTRAINT admin_notification_targets_notification_id_admin_id_uidx UNIQUE (notification_id, admin_id)
);

COMMENT ON TABLE sys.admin_notification_targets IS '管理员通知定向接收人';

COMMENT ON COLUMN sys.admin_notification_targets.id IS '主键';

COMMENT ON COLUMN sys.admin_notification_targets.notification_id IS '通知ID';

COMMENT ON COLUMN sys.admin_notification_targets.admin_id IS '管理员ID';

COMMENT ON COLUMN sys.admin_notification_targets.created_at IS '创建时间';

CREATE INDEX admin_notification_targets_admin_id_idx ON sys.admin_notification_targets (admin_id);

CREATE INDEX admin_notification_targets_notification_id_idx ON sys.admin_notification_targets (notification_id);

-- rollback drop table sys.admin_notification_targets;
