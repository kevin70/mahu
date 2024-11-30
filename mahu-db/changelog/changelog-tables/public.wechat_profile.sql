-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211412
create table public.wechat_profile
(
    id          bigint,                      -- 用户 ID
    create_time timestamp without time zone, -- 创建时间
    update_time timestamp without time zone, -- 更新时间
    appid       character varying(64),       -- 微信应用 ID
    openid      character varying(64),       -- 微信 OpenID
    unionid     character varying(64)        -- 微信帐户唯一 ID
);

comment on column public.wechat_profile.id is '用户 ID';
comment on column public.wechat_profile.create_time is '创建时间';
comment on column public.wechat_profile.update_time is '更新时间';
comment on column public.wechat_profile.appid is '微信应用 ID';
comment on column public.wechat_profile.openid is '微信 OpenID';
comment on column public.wechat_profile.unionid is '微信帐户唯一 ID';

create unique index wechat_profile_appid_openid_uidx on public.wechat_profile using btree (appid, openid);
create unique index wechat_profile_unionid_uidx on public.wechat_profile using btree (unionid);
-- rollback drop table public.wechat_profile;
