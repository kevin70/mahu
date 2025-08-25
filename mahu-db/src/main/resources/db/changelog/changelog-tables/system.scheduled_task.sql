-- liquibase formatted sql


-- changeset kzou227@qq.com:202508242200
create table system.scheduled_task
(
    task_name            varchar(50) not null,
    task_instance        varchar(50) not null
        constraint scheduled_task_pk primary key,
    task_data            bytea,
    execution_time       timestamp without time zone not null,
    picked               boolean     not null,
    picked_by            varchar(255),
    last_success         timestamp,
    last_failure         timestamp,
    consecutive_failures integer,
    last_heartbeat       timestamp without time zone,
    version              bigint      not null,
    priority             smallint
);

comment
on table system.scheduled_task is '定时任务：https://github.com/kagkarlsson/db-scheduler';

comment
on column system.scheduled_task.task_name is '任务名称';

comment
on column system.scheduled_task.task_data is '任务数据';

create index scheduled_task_execution_time_i
    on system.scheduled_task (execution_time);

create index scheduled_task_last_heartbeat_i
    on system.scheduled_task (last_heartbeat);
-- rollback drop table system.scheduled_task;
