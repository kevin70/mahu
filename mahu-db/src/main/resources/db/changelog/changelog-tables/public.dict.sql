-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251125
create table public.dict_type
(
    id          varchar(50) not null
        constraint dict_type_pk
            primary key,
    name        varchar(255),
    description varchar(4096),
    disabled    "char",
    created_at  timestamp,
    updated_at  timestamp
);

comment on table public.dict_type is '字典类型';

comment on column public.dict_type.id is '字典类型 ID';

comment on column public.dict_type.name is '字典类型名称';

comment on column public.dict_type.disabled is '状态（T:禁用, F:启用）';

comment on column public.dict_type.created_at is '创建时间';

comment on column public.dict_type.updated_at is '更新时间';

comment on column public.dict_type.description is '描述';
-- rollback drop table public.dict_type;


-- changeset kzou227@qq.com:202508251126
create table public.dict
(
    dc           serial      not null
        constraint dict_pk primary key,
    label        varchar(255),
    value        varchar(4096),
    disabled     "char",
    ordering     integer,
    dict_type_id varchar(50) not null,
    created_at   timestamp,
    updated_at   timestamp
);

comment on table public.dict is '字典数据';

comment on column public.dict.dc is '字典代码';

comment on column public.dict.dict_type_id is '字典类型代码';

comment on column public.dict.label is '字典数据文本';

comment on column public.dict.value is '字典数据值';

comment on column public.dict.ordering is '排序值';

comment on column public.dict.disabled is '状态（T:禁用, F:启用）';

comment on column public.dict.created_at is '创建时间';

comment on column public.dict.updated_at is '更新时间';

create index dict_type_i on public.dict (dict_type_id);

alter sequence public.dict_dc_seq restart with 1001;
-- rollback drop table public.dict;
