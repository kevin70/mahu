-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211409
create table public.user_attrs
(
    user_id  bigint not null
        constraint user_attrs_pk
            primary key,
    ver      integer,
    balance  numeric(20, 4),
    integral bigint
);

comment
on column public.user_attrs.user_id is '用户 ID';

comment
on column public.user_attrs.ver is '乐观锁';

comment
on column public.user_attrs.balance is '余额';

comment
on column public.user_attrs.integral is '积分';

create unique index user_attrs_user_id_uidx
    on public.user_attrs (user_id);
-- rollback drop table public.user_attrs;


-- changeset kzou227@qq.com:202408071543
alter table public.user_attrs
    add address_count smallint;

comment on column public.user_attrs.address_count is '用户地址数量';
-- rollback alter table public.user_attrs drop column address_count;
