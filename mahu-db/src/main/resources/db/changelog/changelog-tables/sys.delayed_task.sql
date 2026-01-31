-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202511232235
CREATE TABLE sys.delayed_task (
  id UUID NOT NULL CONSTRAINT delayed_task_pk PRIMARY KEY,
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

comment ON TABLE sys.delayed_task IS '延时任务';

comment ON COLUMN sys.delayed_task.id IS '主键';

comment ON COLUMN sys.delayed_task.created_at IS '创建时间';

comment ON COLUMN sys.delayed_task.updated_at IS '更新时间';

comment ON COLUMN sys.delayed_task.feature_id IS '功能 ID（用于追踪统计）';

comment ON COLUMN sys.delayed_task.topic IS '主题';

comment ON COLUMN sys.delayed_task.status IS '状态
- 11 待处理
- 50 进行中
- 88 已完成
- 95 已归档';

comment ON COLUMN sys.delayed_task.delay_until IS '下一次可执行时间（含重试）';

comment ON COLUMN sys.delayed_task.attempts IS '已尝试次数';

comment ON COLUMN sys.delayed_task.max_attempts IS '最大尝试次数（包括首次执行）';

comment ON COLUMN sys.delayed_task.lock_at IS '锁定时间';

comment ON COLUMN sys.delayed_task.lease_seconds IS '锁租约(秒)，worker 处理任务允许的最长时间';

comment ON COLUMN sys.delayed_task.payload IS '消息内容存储业务所需的所有数据';

comment ON COLUMN sys.delayed_task.idempotency_key IS '幂等键';

CREATE UNIQUE INDEX delayed_task_topic_idempotency_key_uidx ON sys.delayed_task (topic, idempotency_key);

CREATE INDEX delayed_task_delay_until_idx ON sys.delayed_task (delay_until)
WHERE
  (status = 11);

CREATE INDEX delayed_task_lock_at_idx ON sys.delayed_task (lock_at)
WHERE
  (status = 50);

-- rollback drop table sys.delay_message;
