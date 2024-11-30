-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211334
create table system.token_jour
(
    id            character varying(32) primary key not null,
    create_time   timestamp without time zone, -- 创建时间
    type          character varying(16),       -- 令牌类型
    sub           character varying(32),       -- 用户唯一名
    client_id     character varying(32),       -- 客户端ID
    client_ip     character varying(64),       -- 客户端IP
    jwk_id        character varying(32),       -- JWT密钥ID
    grant_type    character varying(32),       -- 授权类型
    access_token  character varying(2048),     -- 访问令牌
    refresh_token character varying(2048)      -- 刷新令牌
);
comment on table system.token_jour is '访问令牌日志';
comment on column system.token_jour.create_time is '创建时间';
comment on column system.token_jour.type is '令牌类型';
comment on column system.token_jour.sub is '用户唯一名';
comment on column system.token_jour.client_id is '客户端ID';
comment on column system.token_jour.client_ip is '客户端IP';
comment on column system.token_jour.jwk_id is 'JWT密钥ID';
comment on column system.token_jour.grant_type is '授权类型';
comment on column system.token_jour.access_token is '访问令牌';
comment on column system.token_jour.refresh_token is '刷新令牌';

create index token_jour_sub_idx on system.token_jour using btree (sub);
-- rollback drop table system.token_jour;
