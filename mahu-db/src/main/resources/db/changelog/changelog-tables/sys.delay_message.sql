-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202511232235
CREATE TABLE sys.delay_message (
  id UUID NOT NULL PRIMARY KEY,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  topic VARCHAR(50),
  status SMALLINT,
  delay_until TIMESTAMP,
  delay INTEGER,
  multiplier SMALLINT,
  max_delay INTEGER,
  max_attempts SMALLINT,
  retry_count SMALLINT,
  body JSONB,
  feature_id INTEGER
);

comment ON TABLE sys.delay_message IS '延迟消息';

comment ON COLUMN sys.delay_message.id IS '主键';

comment ON COLUMN sys.delay_message.created_at IS '创建时间';

comment ON COLUMN sys.delay_message.updated_at IS '更新时间';

comment ON COLUMN sys.delay_message.topic IS '主题';

comment ON COLUMN sys.delay_message.status IS '状态';

comment ON COLUMN sys.delay_message.delay_until IS '消息延迟到的绝对时间（精确到毫秒）';

comment ON COLUMN sys.delay_message.delay IS '初始重试间隔（1000 毫秒，即 1 秒）';

comment ON COLUMN sys.delay_message.multiplier IS '重试间隔的倍数（指数退避策略，间隔依次为 1s、2s、4s、8s...）';

comment ON COLUMN sys.delay_message.max_delay IS '最大重试间隔（10000 毫秒，即 10 秒，避免间隔无限增大）';

comment ON COLUMN sys.delay_message.max_attempts IS '最大重试次数（包括首次执行）';

comment ON COLUMN sys.delay_message.retry_count IS '重试次数';

comment ON COLUMN sys.delay_message.body IS '消息内容';

comment ON COLUMN sys.delay_message.feature_id IS '功能 ID（用于追踪统计）';

CREATE INDEX delay_message_status_delay_until_i ON sys.delay_message (status, delay_until);

CREATE INDEX delay_message_topic_i ON sys.delay_message (topic);

-- rollback drop table sys.delay_message;
