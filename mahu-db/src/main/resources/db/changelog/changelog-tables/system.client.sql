-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251132
create table system.client
(
    client_id        varchar(50) not null
        constraint client_pk
            primary key,
    created_at       timestamp without time zone,
    updated_at       timestamp without time zone,
    deleted          "char" default 'F'::"char",
    client_secret    varchar(4000),
    remark           varchar(500),
    label            varchar(128),
    terminal_type    varchar(32),
    wechat_appid     varchar(128),
    wechat_appsecret varchar(512)
);

comment
on table system.client is '认证客户端';

comment
on column system.client.client_id is '客户端 ID';

comment
on column system.client.created_at is '创建时间';

comment
on column system.client.updated_at is '修改时间';

comment
on column system.client.deleted is '软删除标识';

comment
on column system.client.client_secret is '客户端密钥';

comment
on column system.client.remark is '备注';

comment
on column system.client.label is '标签';

comment
on column system.client.terminal_type is '终端类型';

comment
on column system.client.wechat_appid is '微信应用 ID';

comment
on column system.client.wechat_appsecret is '微信应用密钥';
-- rollback drop table system.client;
