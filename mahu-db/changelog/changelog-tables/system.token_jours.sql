-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211334
create table system.token_jours
(
    id            varchar(32) not null
        constraint token_jour_pk
            primary key,
    create_time   timestamp,
    type          varchar(16),
    sub           varchar(32),
    client_id     varchar(32),
    client_ip     varchar(64),
    jwk_id        varchar(32),
    grant_type    varchar(32),
    access_token  varchar(2048),
    refresh_token varchar(2048)
);

comment
on table system.token_jours is '访问令牌日志';

comment
on column system.token_jours.create_time is '创建时间';

comment
on column system.token_jours.type is '令牌类型';

comment
on column system.token_jours.sub is '用户唯一名';

comment
on column system.token_jours.client_id is '客户端ID';

comment
on column system.token_jours.client_ip is '客户端IP';

comment
on column system.token_jours.jwk_id is 'JWT密钥ID';

comment
on column system.token_jours.grant_type is '授权类型';

comment
on column system.token_jours.access_token is '访问令牌';

comment
on column system.token_jours.refresh_token is '刷新令牌';

create index token_jour_sub_idx
    on system.token_jours (sub);
-- rollback drop table system.token_jours;
