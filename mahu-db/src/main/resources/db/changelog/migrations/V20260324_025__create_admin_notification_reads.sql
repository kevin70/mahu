-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-025
CREATE TABLE sys.admin_notification_reads (
  id BIGINT GENERATED ALWAYS AS IDENTITY CONSTRAINT admin_notification_reads_pk PRIMARY KEY,
  notification_id BIGINT NOT NULL,
  admin_id INTEGER NOT NULL,
  read_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT admin_notification_reads_notification_id_admin_id_uidx UNIQUE (notification_id, admin_id)
);

COMMENT ON TABLE sys.admin_notification_reads IS '管理员通知已读记录';

COMMENT ON COLUMN sys.admin_notification_reads.id IS '主键';

COMMENT ON COLUMN sys.admin_notification_reads.notification_id IS '通知ID';

COMMENT ON COLUMN sys.admin_notification_reads.admin_id IS '管理员ID';

COMMENT ON COLUMN sys.admin_notification_reads.read_at IS '已读时间';

CREATE INDEX admin_notification_reads_admin_id_read_at_idx ON sys.admin_notification_reads (admin_id, read_at DESC);

CREATE INDEX admin_notification_reads_notification_id_idx ON sys.admin_notification_reads (notification_id);

-- rollback drop table sys.admin_notification_reads;
