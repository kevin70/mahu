-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251108
CREATE TABLE sys.delayed_tasks (
  id UUID NOT NULL CONSTRAINT delayed_tasks_pk PRIMARY KEY,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  feature_id INTEGER,
  topic VARCHAR(50),
  status SMALLINT,
  delay_until TIMESTAMP,
  attempts INTEGER,
  max_attempts SMALLINT,
  lock_at TIMESTAMP,
  lease_seconds SMALLINT,
  payload JSONB,
  idempotency_key VARCHAR(128)
);

COMMENT ON TABLE sys.delayed_tasks IS '延时任务';

COMMENT ON COLUMN sys.delayed_tasks.id IS '主键';

COMMENT ON COLUMN sys.delayed_tasks.created_at IS '创建时间';

COMMENT ON COLUMN sys.delayed_tasks.updated_at IS '更新时间';

COMMENT ON COLUMN sys.delayed_tasks.feature_id IS '功能 ID（用于追踪统计）';

COMMENT ON COLUMN sys.delayed_tasks.topic IS '主题';

COMMENT ON COLUMN sys.delayed_tasks.status IS '状态
- 11 待处理
- 50 进行中
- 88 已完成
- 95 已归档';

COMMENT ON COLUMN sys.delayed_tasks.delay_until IS '下一次可执行时间（含重试）';

COMMENT ON COLUMN sys.delayed_tasks.attempts IS '已尝试次数';

COMMENT ON COLUMN sys.delayed_tasks.max_attempts IS '最大尝试次数（包括首次执行）';

COMMENT ON COLUMN sys.delayed_tasks.lock_at IS '锁定时间';

COMMENT ON COLUMN sys.delayed_tasks.lease_seconds IS '锁租约(秒)，worker 处理任务允许的最长时间';

COMMENT ON COLUMN sys.delayed_tasks.payload IS '消息内容存储业务所需的所有数据';

COMMENT ON COLUMN sys.delayed_tasks.idempotency_key IS '幂等键';

CREATE UNIQUE INDEX delayed_tasks_topic_idempotency_key_uidx ON sys.delayed_tasks (topic, idempotency_key);

CREATE INDEX delayed_tasks_delay_until_idx ON sys.delayed_tasks (delay_until)
WHERE
  (status = 11);

CREATE INDEX delayed_tasks_lock_at_idx ON sys.delayed_tasks (lock_at)
WHERE
  (status = 50);

-- rollback drop table sys.delay_message;
