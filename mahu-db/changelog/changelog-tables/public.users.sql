-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211408
create table public.users
(
    id          bigserial primary key,       -- 用户ID
    create_time timestamp without time zone, -- 创建时间
    update_time timestamp without time zone, -- 更新时间
    deleted     "char",                      -- 软删除
    avatar      character varying(1024),     -- 头像地址
    nickname    character varying(128),      -- 昵称
    mobile      character varying(20)        -- 手机号码
);
comment on table public.users is '用户表';
comment on column public.users.id is '用户ID';
comment on column public.users.create_time is '创建时间';
comment on column public.users.update_time is '更新时间';
comment on column public.users.deleted is '软删除';
comment on column public.users.avatar is '头像地址';
comment on column public.users.nickname is '昵称';
comment on column public.users.mobile is '手机号码';

alter sequence public.users_id_seq restart with 100001;
-- rollback drop table public.users;
