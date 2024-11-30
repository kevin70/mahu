-- liquibase formatted sql


-- changeset kzou227@qq.com:202406031023
create table system.audit_jour
(
    id             bigserial,                   -- 主键
    create_time    timestamp without time zone, -- 创建时间
    source         character varying(128),      -- 来源
    user_id        bigint,                      -- 用户 ID
    ip_addr        character varying(64),       -- 操作 IP 地址
    table_name     character varying(128),      -- 改变的表名
    data_tenant_id character varying(128),      -- 租佃 ID
    change_type    "char",                      -- 修改类型
    data_id        character varying(256),      -- 数据 ID
    data           jsonb                        -- 更新数据
);
comment on column system.audit_jour.id is '主键';
comment on column system.audit_jour.create_time is '创建时间';
comment on column system.audit_jour.source is '来源';
comment on column system.audit_jour.user_id is '用户 ID';
comment on column system.audit_jour.ip_addr is '操作 IP 地址';
comment on column system.audit_jour.table_name is '改变的表名';
comment on column system.audit_jour.data_tenant_id is '租佃 ID';
comment on column system.audit_jour.change_type is '修改类型';
comment on column system.audit_jour.data_id is '数据 ID';
comment on column system.audit_jour.data is '更新数据';

create index audit_jour_table_name_data_id_idx on system.audit_jour using btree (table_name, data_id);
create index audit_jour_ip_addr_idx on system.audit_jour using btree (ip_addr);
create index audit_jour_user_id_idx on system.audit_jour using btree (user_id);
-- rollback drop table system.audit_jour;
