-- liquibase formatted sql


-- changeset kzou227@qq.com:0KPYD7H65XP60
create table log.scheduled_execution_log
(
    id            bigint not null
        constraint scheduled_execution_log_pk
            primary key,
    created_at    timestamp without time zone,
    task_name     varchar(50),
    task_instance varchar(50),
    picked_by     varchar(50),
    started_at    timestamp without time zone,
    finished_at   timestamp without time zone,
    succeeded     "char",
    cause         jsonb
);

comment
    on column log.scheduled_execution_log.id is '主键';

comment
    on column log.scheduled_execution_log.created_at is '创建时间';

comment
    on column log.scheduled_execution_log.task_name is '任务名称';

comment
    on column log.scheduled_execution_log.task_instance is '执行实例';

comment
    on column log.scheduled_execution_log.picked_by is '任务执行者';

comment
    on column log.scheduled_execution_log.started_at is '任务开始时间';

comment
    on column log.scheduled_execution_log.finished_at is '任务结束时间';

comment
    on column log.scheduled_execution_log.succeeded is '执行是否成功';

comment
    on column log.scheduled_execution_log.cause is '失败原因';

create index task_name_idx
    on log.scheduled_execution_log (task_name);
-- rollback drop table log.scheduled_execution_log;
