-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251106
CREATE TABLE sys.scheduled_tasks (
  task_name VARCHAR(50) NOT NULL CONSTRAINT scheduled_tasks_pk PRIMARY KEY,
  task_instance VARCHAR(50) NOT NULL,
  task_data bytea,
  execution_time TIMESTAMP NOT NULL,
  picked BOOLEAN NOT NULL,
  picked_by VARCHAR(255),
  last_success TIMESTAMP,
  last_failure TIMESTAMP,
  consecutive_failures INTEGER,
  last_heartbeat TIMESTAMP,
  version BIGINT NOT NULL,
  priority SMALLINT
);

COMMENT ON TABLE sys.scheduled_tasks IS '定时任务：https://github.com/kagkarlsson/db-scheduler';

COMMENT ON COLUMN sys.scheduled_tasks.task_name IS '任务名称';

COMMENT ON COLUMN sys.scheduled_tasks.task_data IS '任务数据';

CREATE INDEX scheduled_tasks_execution_time_idx ON sys.scheduled_tasks (execution_time);

CREATE INDEX scheduled_tasks_last_heartbeat_idx ON sys.scheduled_tasks (last_heartbeat);

-- rollback drop table sys.scheduled_tasks;
