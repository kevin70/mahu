-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251148
create table sys.admin_auth_log
(
    id         bigint not null
        constraint admin_auth_log_pk
            primary key,
    created_at timestamp,
    admin_id   bigint,
    grant_type varchar(50),
    client_id  varchar(50),
    ip_addr    varchar(50),
    user_agent varchar(1024)
);

comment on table sys.admin_auth_log is '管理员认证日志';

comment on column sys.admin_auth_log.id is '主键';

comment on column sys.admin_auth_log.created_at is '创建时间';

comment on column sys.admin_auth_log.admin_id is '管理员用户 ID';

comment on column sys.admin_auth_log.grant_type is '认证类型
PASSWORD
TOKEN_REFRESH';

comment on column sys.admin_auth_log.client_id is '认证客户端 ID';

comment on column sys.admin_auth_log.ip_addr is '认证 IP';

comment on column sys.admin_auth_log.user_agent is '认证的 User Agent';

create index admin_auth_log_admin_id_i
    on sys.admin_auth_log (admin_id);
-- rollback drop table sys.admin_auth_log;
