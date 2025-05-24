-- liquibase formatted sql


-- changeset kzou227@qq.com:0KPYD7H65XP63
create table public.users
(
    id             bigserial
        constraint users_pk
            primary key,
    created_at     timestamp without time zone,
    updated_at     timestamp without time zone,
    deleted        "char" default 'F'::"char",
    avatar         varchar(1024),
    nickname       varchar(128),
    mobile         varchar(20),
    username       varchar(128),
    password       varchar(2048),
    status         smallint,
    wechat_appid   varchar(64),
    wechat_openid  varchar(64),
    wechat_unionid varchar(64)
);

comment
    on table public.users is '用户表';

comment
    on column public.users.id is '用户ID';

comment
    on column public.users.created_at is '创建时间';

comment
    on column public.users.updated_at is '更新时间';

comment
    on column public.users.deleted is '软删除';

comment
    on column public.users.avatar is '头像地址';

comment
    on column public.users.nickname is '昵称';

comment
    on column public.users.mobile is '手机号码';

comment
    on column public.users.username is '登录名';

comment
    on column public.users.password is '登录密码';

comment
    on column public.users.status is '状态';

comment
    on column public.users.wechat_appid is '微信 appid';

comment
    on column public.users.wechat_openid is '微信 openid';

comment
    on column public.users.wechat_unionid is '微信 unionid';

alter sequence public.users_id_seq restart with 100001;
-- rollback drop table public.users;
