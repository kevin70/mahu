-- liquibase formatted sql


-- changeset kzou227@qq.com:0KPY6J2C5XP5M
create table system.dict_type
(
    type_code   varchar(50) not null
        constraint dict_type_pk primary key,
    name        varchar(100),
    description varchar(255),
    disabled    "char",
    created_at  timestamp without time zone,
    updated_at  timestamp without time zone
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


-- changeset kzou227@qq.com:0KPY6J2C5XP5N
create table system.dict_data
(
    id         bigserial
        constraint dict_data_pk primary key,
    type_code  varchar(50) not null,
    data_code  varchar(50) not null,
    name       varchar(100),
    value      varchar(4096),
    ordering   integer,
    disabled   "char",
    created_at timestamp,
    updated_at timestamp
);

comment
    on table system.dict_data is '字典数据';

comment
    on column system.dict_data.type_code is '类型代码';

comment
    on column system.dict_data.data_code is '字典数据编码';

comment
    on column system.dict_data.name is '字典数据名称';

comment
    on column system.dict_data.value is '字典数据值';

comment
    on column system.dict_data.ordering is '排序值';

comment
    on column system.dict_data.disabled is '状态（T:禁用, F:启用）';

comment
    on column system.dict_data.created_at is '创建时间';

comment
    on column system.dict_data.updated_at is '更新时间';

create unique index dict_type_code_data_code_ui
    on system.dict_data (type_code, data_code);
-- rollback drop table system.dict_data;
