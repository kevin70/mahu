-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251147
create table system.admin_audit_log
(
    id             bigint not null
        constraint admin_audit_log_pk
            primary key,
    created_at     timestamp without time zone,
    admin_id       bigint,
    ip_addr        varchar(50),
    change_type    "char",
    table_name     varchar(50),
    data_tenant_id varchar(50),
    data_id        varchar(256),
    data           jsonb,
    old_data       jsonb
);

comment
    on table system.admin_audit_log is '管理员修改操作审计日志';

comment
    on table system.admin_audit_log is '管理员操作审计日志';

comment
    on column system.admin_audit_log.id is '主键';

comment
    on column system.admin_audit_log.created_at is '创建时间';

comment
    on column system.admin_audit_log.admin_id is '管理员 ID';

comment
    on column system.admin_audit_log.ip_addr is '操作 IP';

comment
    on column system.admin_audit_log.change_type is '改变类型';

comment
    on column system.admin_audit_log.table_name is '数据库表名';

comment
    on column system.admin_audit_log.data_tenant_id is '数据租户 ID';

comment
    on column system.admin_audit_log.data_id is '数据主键';

comment
    on column system.admin_audit_log.data is '改变的数据';

comment
    on column system.admin_audit_log.old_data is '旧的数据';

create index admin_audit_log_admin_id_i
    on system.admin_audit_log (admin_id);
-- rollback drop table system.admin_audit_log;
