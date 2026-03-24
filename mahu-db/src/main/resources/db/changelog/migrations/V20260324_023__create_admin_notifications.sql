-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-023
CREATE TABLE sys.admin_notifications (
  id BIGINT GENERATED ALWAYS AS IDENTITY CONSTRAINT admin_notifications_pk PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  scope SMALLINT NOT NULL,
  type SMALLINT NOT NULL,
  status SMALLINT NOT NULL DEFAULT 22,
  payload JSONB NOT NULL DEFAULT '{}'::JSONB,
  expire_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ
);

COMMENT ON TABLE sys.admin_notifications IS '管理员通知主表';
COMMENT ON COLUMN sys.admin_notifications.id IS '主键';
COMMENT ON COLUMN sys.admin_notifications.title IS '通知标题';
COMMENT ON COLUMN sys.admin_notifications.content IS '通知内容';
COMMENT ON COLUMN sys.admin_notifications.scope IS '发送范围：1定向，2全局';
COMMENT ON COLUMN sys.admin_notifications.type IS '通知类型';
COMMENT ON COLUMN sys.admin_notifications.status IS '状态：22生效，90过期';
COMMENT ON COLUMN sys.admin_notifications.payload IS '扩展载荷（JSON对象）';
COMMENT ON COLUMN sys.admin_notifications.expire_at IS '过期时间，NULL表示不过期';
COMMENT ON COLUMN sys.admin_notifications.created_at IS '创建时间';
COMMENT ON COLUMN sys.admin_notifications.updated_at IS '更新时间';

CREATE INDEX admin_notifications_status_expire_at_idx ON sys.admin_notifications (status, expire_at)
WHERE
  (status = 22);
CREATE INDEX admin_notifications_type_created_at_idx ON sys.admin_notifications (type, created_at DESC);
CREATE INDEX admin_notifications_created_at_idx ON sys.admin_notifications (created_at DESC);

-- rollback drop table sys.admin_notifications;
