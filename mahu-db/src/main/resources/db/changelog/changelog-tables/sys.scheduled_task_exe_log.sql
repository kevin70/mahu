-- liquibase formatted sql


-- changeset kzou227@qq.com:202509172017
create table sys.scheduled_task_exe_log
(
    id         varchar(50) not null
        constraint scheduled_task_exe_log_pk
            primary key,
    created_at timestamp,
    task_name  varchar(50),
    picked_by  varchar(50),
    start_time timestamp,
    done_time  timestamp,
    success    "char",
    trace_id   varchar(50),
    fail_cause jsonb
);

comment on table sys.scheduled_task_exe_log is '定时任务执行记录';

comment on column sys.scheduled_task_exe_log.id is '主键';

comment on column sys.scheduled_task_exe_log.created_at is '创建时间';

comment on column sys.scheduled_task_exe_log.task_name is '任务名称';

comment on column sys.scheduled_task_exe_log.picked_by is '任务执行者';

comment on column sys.scheduled_task_exe_log.start_time is '任务开始时间';

comment on column sys.scheduled_task_exe_log.done_time is '任务完成时间';

comment on column sys.scheduled_task_exe_log.success is '是否执行成功';

comment on column sys.scheduled_task_exe_log.trace_id is '日志追踪 ID';

comment on column sys.scheduled_task_exe_log.fail_cause is '失败原因';

create index scheduled_task_exe_log_task_name_i
    on sys.scheduled_task_exe_log (task_name);
-- rollback drop table sys.scheduled_task_exe_log;
