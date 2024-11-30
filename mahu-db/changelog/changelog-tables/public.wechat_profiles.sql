-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211412
create table public.wechat_profiles
(
    id          bigserial
        primary key,
    create_time timestamp,
    update_time timestamp,
    user_id     bigint not null,
    appid       varchar(64),
    openid      varchar(64),
    unionid     varchar(64)
);

comment
on column public.wechat_profiles.id is '主键';

comment
on column public.wechat_profiles.create_time is '创建时间';

comment
on column public.wechat_profiles.update_time is '更新时间';

comment
on column public.wechat_profiles.user_id is '用户ID';

comment
on column public.wechat_profiles.appid is '微信应用 ID';

comment
on column public.wechat_profiles.openid is '微信 OpenID';

comment
on column public.wechat_profiles.unionid is '微信帐户唯一 ID';

create unique index wechat_profiles_appid_openid_uidx
    on public.wechat_profiles (appid, openid);

create unique index wechat_profiles_unionid_uidx
    on public.wechat_profiles (unionid);

create index wechat_profiles_user_id_index
    on public.wechat_profiles (user_id);
-- rollback drop table public.wechat_profiles;


-- changeset kzou227@qq.com:202404011744
alter table public.wechat_profiles
    add sub_message_templates varchar[];

comment
on column public.wechat_profiles.sub_message_templates is '订阅的消息模板 IDs';
-- rollback alter table public.wechat_profiles drop column sub_message_templates;
