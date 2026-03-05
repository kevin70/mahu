-- liquibase formatted sql
;

CREATE TABLE sys.scheduled_task_exe_logs (
  id UUID NOT NULL CONSTRAINT scheduled_task_exe_logs_pk PRIMARY KEY,
  created_at TIMESTAMP,
  task_name VARCHAR(50),
  picked_by VARCHAR(50),
  start_time TIMESTAMP,
  done_time TIMESTAMP,
  success BOOLEAN,
  trace_id VARCHAR(26),
  fail_cause JSONB
);

COMMENT ON TABLE sys.scheduled_task_exe_logs IS '定时任务执行记录';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.id IS '主键';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.created_at IS '创建时间';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.task_name IS '任务名称';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.picked_by IS '任务执行者';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.start_time IS '任务开始时间';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.done_time IS '任务完成时间';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.success IS '是否执行成功';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.trace_id IS '日志追踪 ID';

COMMENT ON COLUMN sys.scheduled_task_exe_logs.fail_cause IS '失败原因';

CREATE INDEX scheduled_task_exe_log_task_name_idx ON sys.scheduled_task_exe_logs (task_name);

-- rollback drop table sys.scheduled_task_exe_logs;
