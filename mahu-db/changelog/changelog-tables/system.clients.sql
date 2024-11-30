-- liquibase formatted sql


-- changeset kzou227@qq.com:202403201912
create table system.clients
(
    id                              serial
        primary key,
    create_time                     timestamp,
    update_time                     timestamp,
    deleted                         "char",
    client_id                       varchar(40)
        unique,
    client_secret                   varchar(4000),
    remark                          varchar(500),
    label                           varchar(128),
    terminal_type                   varchar(32),
    wechat_appid                    varchar(128),
    wechat_appsecret                varchar(512),
    wechat_access_token             varchar(512),
    wechat_access_token_expire_time timestamp
);

comment
on table system.clients is '认证客户端';

comment
on column system.clients.create_time is '创建时间';

comment
on column system.clients.update_time is '修改时间';

comment
on column system.clients.deleted is '软删除标识';

comment
on column system.clients.client_id is '客户端 ID';

comment
on column system.clients.client_secret is '客户端密钥';

comment
on column system.clients.remark is '备注';

comment
on column system.clients.label is '标签';

comment
on column system.clients.terminal_type is '终端类型';

comment
on column system.clients.wechat_appid is '微信应用 ID';

comment
on column system.clients.wechat_appsecret is '微信应用密钥';

comment
on column system.clients.wechat_access_token is '微信客户端访问令牌';

comment
on column system.clients.wechat_access_token_expire_time is '微信访问令牌过期时间';

alter sequence system.clients_id_seq restart with 30001;
-- rollback drop table system.clients;


-- changeset kzou227@qq.com:202403211454
-- common: 客户端基础数据
INSERT INTO system.clients (id, create_time, update_time, deleted, client_id, client_secret, remark)
VALUES (200, current_timestamp, current_timestamp, 'F',
        'QKlHUNPmV8mXdbHAplQLTqYIVqhPYGcl',
        'm6+&AdwG<t!<o^BK!_VrO~`o(!kkz0;Q1g1UXH4GS.''D[>w`qN#qXR]P3h;8A3Up7uWgfRG!''Q)to!5^@e+?Ng|t<r8`~0aZDCI%WbM[ga_JAyQK6v?G9gSlgRES0,2G',
        '测试专用');
-- rollback delete from system.clients where id = 200;
