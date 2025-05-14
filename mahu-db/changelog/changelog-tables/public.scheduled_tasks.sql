-- liquibase formatted sql


-- changeset kzou227@qq.com:0KPY6J2C5XP5T
create table public.scheduled_tasks
(
    task_name            varchar(50) not null,
    task_instance        varchar(50) not null,
    task_data            bytea,
    execution_time       timestamp   not null,
    picked               boolean     not null,
    picked_by            varchar(255),
    last_success         timestamp,
    last_failure         timestamp,
    consecutive_failures integer,
    last_heartbeat       timestamp,
    version              bigint      not null,
    priority             smallint,
    constraint scheduled_tasks_pk
        primary key (task_name, task_instance)
);

comment
on table public.scheduled_tasks is '定时任务：https://github.com/kagkarlsson/db-scheduler';

comment
on column public.scheduled_tasks.task_name is '任务名称';

comment
on column public.scheduled_tasks.task_data is '任务数据';

create index execution_time_idx
    on public.scheduled_tasks (execution_time);

create index priority_execution_time_idx
    on public.scheduled_tasks (priority desc, execution_time asc);

create index last_heartbeat_idx
    on public.scheduled_tasks (last_heartbeat);
-- rollback drop table public.scheduled_tasks;
