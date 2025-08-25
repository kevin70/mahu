-- liquibase formatted sql


-- changeset kzou227@qq.com:202508250009
create table log.scheduled_execution_log
(
    id          bigint not null
        constraint scheduled_execution_log_pk
            primary key,
    created_at  timestamp without time zone,
    task_id     varchar(50),
    picked_by   varchar(50),
    started_at  timestamp without time zone,
    finished_at timestamp without time zone,
    succeeded   "char",
    trace_id    varchar(50),
    cause       jsonb
);

comment
on column log.scheduled_execution_log.id is '主键';

comment
on column log.scheduled_execution_log.created_at is '创建时间';

comment
on column log.scheduled_execution_log.task_id is '任务 ID';

comment
on column log.scheduled_execution_log.picked_by is '任务执行者';

comment
on column log.scheduled_execution_log.started_at is '任务开始时间';

comment
on column log.scheduled_execution_log.finished_at is '任务结束时间';

comment
on column log.scheduled_execution_log.succeeded is '执行是否成功';

comment
on column log.scheduled_execution_log.trace_id is '日志追踪 ID';

comment
on column log.scheduled_execution_log.cause is '失败原因';

create index task_id_i
    on log.scheduled_execution_log (task_id);
-- rollback drop table log.scheduled_execution_log;
