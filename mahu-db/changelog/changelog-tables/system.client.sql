-- liquibase formatted sql


-- changeset kzou227@qq.com:202403201912
create table system.client
(
    client_id                       character varying(32) primary key not null, -- 客户端 ID
    create_time                     timestamp without time zone,                -- 创建时间
    update_time                     timestamp without time zone,                -- 修改时间
    deleted                         "char",                                     -- 软删除标识
    client_secret                   character varying(4000),                    -- 客户端密钥
    remark                          character varying(500),                     -- 备注
    label                           character varying(128),                     -- 标签
    terminal_type                   character varying(32),                      -- 终端类型
    wechat_appid                    character varying(128),                     -- 微信应用 ID
    wechat_appsecret                character varying(512),                     -- 微信应用密钥
    wechat_access_token             character varying(512),                     -- 微信客户端访问令牌
    wechat_access_token_expire_time timestamp without time zone                 -- 微信访问令牌过期时间
);
comment on table system.client is '认证客户端';
comment on column system.client.client_id is '客户端 ID';
comment on column system.client.create_time is '创建时间';
comment on column system.client.update_time is '修改时间';
comment on column system.client.deleted is '软删除标识';
comment on column system.client.client_secret is '客户端密钥';
comment on column system.client.remark is '备注';
comment on column system.client.label is '标签';
comment on column system.client.terminal_type is '终端类型';
comment on column system.client.wechat_appid is '微信应用 ID';
comment on column system.client.wechat_appsecret is '微信应用密钥';
comment on column system.client.wechat_access_token is '微信客户端访问令牌';
comment on column system.client.wechat_access_token_expire_time is '微信访问令牌过期时间';
-- rollback drop table system.client;


-- changeset kzou227@qq.com:202403211454
-- common: 客户端基础数据
INSERT INTO system.client (create_time, update_time, deleted, client_id, client_secret, remark)
VALUES (current_timestamp, current_timestamp, 'F',
        '01JCTAWH7NZRHHJZG9DPQ1SFFG',
        'y_6aEKTb_AjOMvQczYY4cwfgRnXvVBCJiPYtmW54HmlHKs7xOS4ZeBfOfK5OmWMnTIztIVnnehD9qspuzUoRXtGj5rNyDq940KtkbiJaI8GNgVaYXpRa9WPpmZBLEX8Eoyl-8vDsgOQOctxieRGJbSmYXmAxMW9t1aqhCo_l459qnINKv8lhj9b-hs3R0EZLZo3VU13aDURSMhF_l4-8rvpOrqzvLxZumK8A3a4dXUwFPI25NGJSaLsGFbqLTBMm',
        '测试专用');
-- rollback delete from system.client where client_id='01JCTAWH7NZRHHJZG9DPQ1SFFG';
