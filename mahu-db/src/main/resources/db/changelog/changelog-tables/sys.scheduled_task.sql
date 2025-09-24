-- liquibase formatted sql
-- changeset kzou227@qq.com:202508242200
CREATE TABLE
  sys.scheduled_task (
    task_name VARCHAR(50) NOT NULL CONSTRAINT scheduled_task_pk PRIMARY KEY,
    task_instance VARCHAR(50) NOT NULL,
    task_data bytea,
    execution_time TIMESTAMP NOT NULL,
    picked BOOLEAN NOT NULL,
    picked_by VARCHAR(255),
    last_success TIMESTAMP,
    last_failure TIMESTAMP,
    consecutive_failures INTEGER,
    last_heartbeat TIMESTAMP,
    VERSION BIGINT NOT NULL,
    priority SMALLINT
  );

COMMENT ON TABLE sys.scheduled_task IS '定时任务：https://github.com/kagkarlsson/db-scheduler';

COMMENT ON COLUMN sys.scheduled_task.task_name IS '任务名称';

COMMENT ON COLUMN sys.scheduled_task.task_data IS '任务数据';

CREATE INDEX scheduled_task_execution_time_i ON sys.scheduled_task (execution_time);

CREATE INDEX scheduled_task_last_heartbeat_i ON sys.scheduled_task (last_heartbeat);

-- rollback drop table sys.scheduled_task;
