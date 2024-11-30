-- liquibase formatted sql


-- changeset kzou227@qq.com:202406031023
create table system.audit_changes
(
    id          bigserial
        primary key,
    create_time timestamp,
    table_name  varchar(64),
    change_type varchar(8),
    user_id     bigint,
    user_addr   varchar(64),
    dpk         varchar(64),
    data        text,
    tenant_id   varchar(64)
);

comment
on column system.audit_changes.id is '主键';

comment
on column system.audit_changes.create_time is '创建时间';

comment
on column system.audit_changes.table_name is '表名称';

comment
on column system.audit_changes.change_type is '变更类型';

comment
on column system.audit_changes.user_id is '变更用户ID';

comment
on column system.audit_changes.user_addr is '操作地址';

comment
on column system.audit_changes.dpk is '数据ID';

comment
on column system.audit_changes.data is '变更的数据';

comment
on column system.audit_changes.tenant_id is '租户ID';
-- rollback drop table system.audit_changes;
