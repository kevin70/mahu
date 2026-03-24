-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-006
CREATE TABLE sys.scheduled_tasks (
  task_name VARCHAR(50) NOT NULL CONSTRAINT scheduled_tasks_pk PRIMARY KEY,
  task_instance VARCHAR(50) NOT NULL,
  task_data bytea,
  execution_time TIMESTAMPTZ NOT NULL,
  picked BOOLEAN NOT NULL,
  picked_by VARCHAR(255),
  last_success TIMESTAMPTZ,
  last_failure TIMESTAMPTZ,
  consecutive_failures INTEGER,
  last_heartbeat TIMESTAMPTZ,
  version BIGINT NOT NULL,
  priority SMALLINT
);

CREATE INDEX scheduled_tasks_execution_time_idx ON sys.scheduled_tasks (execution_time);

CREATE INDEX scheduled_tasks_last_heartbeat_idx ON sys.scheduled_tasks (last_heartbeat);

-- rollback drop table sys.scheduled_tasks;
