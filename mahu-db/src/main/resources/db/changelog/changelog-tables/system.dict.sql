-- liquibase formatted sql


-- changeset kzou227@qq.com:202508251125
create table system.dict_type
(
    type_code   character varying(50)
        constraint dict_type_pk primary key not null, -- 字典类型代码
    name        character varying(255),               -- 字典类型名称
    description character varying(255),               -- 字典类型描述
    disabled    "char",                               -- 状态（T:禁用, F:启用）
    created_at  timestamp without time zone,          -- 创建时间
    updated_at  timestamp without time zone           -- 更新时间
);

comment
on table system.dict_type is '字典类型';
comment
on column system.dict_type.type_code is '字典类型代码';
comment
on column system.dict_type.name is '字典类型名称';
comment
on column system.dict_type.description is '字典类型描述';
comment
on column system.dict_type.disabled is '状态（T:禁用, F:启用）';
comment
on column system.dict_type.created_at is '创建时间';
comment
on column system.dict_type.updated_at is '更新时间';
-- rollback drop table system.dict_type;


-- changeset kzou227@qq.com:202508251126
create table system.dict
(
    code       character varying(50)
        constraint dict_pk primary key not null, -- 字典代码
    type_code  character varying(50)   not null, -- 类型代码
    name       character varying(255),           -- 字典数据名称
    value      character varying(4096),          -- 字典数据值
    ordering   integer,                          -- 排序值
    disabled   "char",                           -- 状态（T:禁用, F:启用）
    created_at timestamp without time zone,      -- 创建时间
    updated_at timestamp without time zone       -- 更新时间
);

create index dict_type_code_i on system.dict using btree (type_code);

comment
on table system.dict is '字典数据';
comment
on column system.dict.code is '字典代码';
comment
on column system.dict.type_code is '类型代码';
comment
on column system.dict.name is '字典数据名称';
comment
on column system.dict.value is '字典数据值';
comment
on column system.dict.ordering is '排序值';
comment
on column system.dict.disabled is '状态（T:禁用, F:启用）';
comment
on column system.dict.created_at is '创建时间';
comment
on column system.dict.updated_at is '更新时间';
-- rollback drop table system.dict;
