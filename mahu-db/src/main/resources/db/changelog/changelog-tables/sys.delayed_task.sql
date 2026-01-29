-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202511232235
create table sys.delayed_task
(
    id              uuid not null
        constraint delayed_task_pk
            primary key,
    created_at      timestamp,
    updated_at      timestamp,
    feature_id      integer,
    topic           varchar(50),
    status          smallint,
    delay_until     timestamp,
    attempts        integer,
    max_attempts    smallint,
    lock_at         timestamp,
    lease_seconds   smallint,
    payload         jsonb,
    idempotency_key varchar(128)
);

comment on table sys.delayed_task is '延迟任务';

comment on column sys.delayed_task.id is '主键';

comment on column sys.delayed_task.created_at is '创建时间';

comment on column sys.delayed_task.updated_at is '更新时间';

comment on column sys.delayed_task.feature_id is '功能 ID（用于追踪统计）';

comment on column sys.delayed_task.topic is '主题';

comment on column sys.delayed_task.status is '状态
- 11 待处理
- 50 进行中
- 88 已完成
- 95 已归档';

comment on column sys.delayed_task.delay_until is '下一次可执行时间（含重试）';

comment on column sys.delayed_task.attempts is '已尝试次数';

comment on column sys.delayed_task.max_attempts is '最大尝试次数（包括首次执行）';

comment on column sys.delayed_task.lock_at is '锁定时间';

comment on column sys.delayed_task.lease_seconds is '锁租约(秒)，worker 处理任务允许的最长时间';

comment on column sys.delayed_task.payload is '消息内容存储业务所需的所有数据';

comment on column sys.delayed_task.idempotency_key is '幂等键';

create unique index delayed_task_topic_idempotency_key_uidx
    on sys.delayed_task (topic, idempotency_key);

create index delayed_task_delay_until_idx
    on sys.delayed_task (delay_until)
    where (status = 11);

create index delayed_task_lock_at_idx
    on sys.delayed_task (lock_at)
    where (status = 50);
-- rollback drop table sys.delay_message;
