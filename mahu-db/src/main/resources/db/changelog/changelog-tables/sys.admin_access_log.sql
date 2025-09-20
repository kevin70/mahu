-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251146
create table sys.admin_access_log
(
    id              bigint not null
        constraint admin_access_log_pk
            primary key,
    created_at      timestamp without time zone,
    admin_id        bigint,
    ip_addr         varchar(64),
    method          varchar(16),
    uri_path        varchar(1024),
    uri_query       varchar(1024),
    referer         varchar(2048),
    protocol        varchar(1024),
    response_status smallint,
    response_bytes  bigint,
    user_agent      varchar(1024)
);

comment
    on table sys.admin_access_log is '管理员访问记录';

comment
    on column sys.admin_access_log.id is '主键';

comment
    on column sys.admin_access_log.created_at is '创建时间';

comment
    on column sys.admin_access_log.admin_id is '管理员 ID';

create index admin_access_log_admin_id_i
    on sys.admin_access_log (admin_id);
-- rollback drop table sys.admin_access_log;
