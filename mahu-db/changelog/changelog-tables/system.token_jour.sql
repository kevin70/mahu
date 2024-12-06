-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211334
create table system.token_jour
(
    id          character varying(32) primary key not null,
    create_time timestamp without time zone, -- 创建时间
    upn         character varying(32),       -- 用户主体
    client_id   character varying(32),       -- 客户端ID
    client_addr character varying(64),       -- 客户端IP
    grant_type  character varying(32)        -- 授权类型
);
create index token_jour_sub_idx on token_jour using btree (upn);
comment
on table system.token_jour is '访问令牌日志';
comment
on column system.token_jour.create_time is '创建时间';
comment
on column system.token_jour.upn is '用户主体';
comment
on column system.token_jour.client_id is '客户端ID';
comment
on column system.token_jour.client_addr is '客户端IP';
comment
on column system.token_jour.grant_type is '授权类型';
-- rollback drop table system.token_jour;
