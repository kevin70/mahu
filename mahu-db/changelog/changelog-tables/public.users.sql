-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211408
create table public.users
(
    id          bigserial
        constraint users_pk
            primary key,
    create_time timestamp,
    update_time timestamp,
    delete_time timestamp,
    deleted     "char",
    avatar      varchar(1024),
    nickname    varchar(128),
    mobile      varchar(20)
);

comment
on table public.users is '用户表';

comment
on column public.users.id is '用户ID';

comment
on column public.users.create_time is '创建时间';

comment
on column public.users.update_time is '更新时间';

comment
on column public.users.delete_time is '删除时间';

comment
on column public.users.deleted is '软删除';

comment
on column public.users.avatar is '头像地址';

comment
on column public.users.nickname is '昵称';

comment
on column public.users.mobile is '手机号码';
-- rollback drop table public.users;


-- changeset kzou227@qq.com:202408071653
alter sequence public.users_id_seq restart with 1000001;
--
