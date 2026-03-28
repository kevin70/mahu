-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-007
CREATE TABLE sys.scheduled_task_logs (
  id UUID NOT NULL CONSTRAINT scheduled_task_logs_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  task_name VARCHAR(50),
  picked_by VARCHAR(50),
  start_time TIMESTAMPTZ,
  done_time TIMESTAMPTZ,
  success BOOLEAN,
  trace_id VARCHAR(26),
  fail_cause JSONB
);

CREATE INDEX scheduled_task_logs_task_name_idx ON sys.scheduled_task_logs (task_name);

-- rollback drop table sys.scheduled_task_logs;
