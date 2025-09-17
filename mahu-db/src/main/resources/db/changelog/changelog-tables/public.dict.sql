-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251125
create table public.dict_type
(
    code       varchar(50) not null
        constraint dict_type_pk
            primary key,
    name       varchar(255),
    disabled   "char",
    created_at timestamp,
    updated_at timestamp
);

comment on table public.dict_type is '字典类型';

comment on column public.dict_type.code is '字典类型代码';

comment on column public.dict_type.name is '字典类型名称';

comment on column public.dict_type.disabled is '状态（T:禁用, F:启用）';

comment on column public.dict_type.created_at is '创建时间';

comment on column public.dict_type.updated_at is '更新时间';
-- rollback drop table public.dict_type;


-- changeset kzou227@qq.com:202508251126
create table public.dict
(
    code       varchar(50) not null
        constraint dict_pk
            primary key,
    type       varchar(50) not null,
    label      varchar(255),
    value      varchar(4096),
    ordering   integer,
    disabled   "char",
    created_at timestamp,
    updated_at timestamp
);

comment on table public.dict is '字典数据';

comment on column public.dict.code is '字典代码';

comment on column public.dict.type is '类型代码';

comment on column public.dict.label is '字典数据文本';

comment on column public.dict.value is '字典数据值';

comment on column public.dict.ordering is '排序值';

comment on column public.dict.disabled is '状态（T:禁用, F:启用）';

comment on column public.dict.created_at is '创建时间';

comment on column public.dict.updated_at is '更新时间';

create index dict_type_i on public.dict (type);
-- rollback drop table public.dict;
