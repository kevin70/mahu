-- liquibase formatted sql


-- changeset kzou227@qq.com:202403201454
create table system.dict
(
    id          serial primary key,          -- 主键
    create_time timestamp without time zone, -- 创建时间
    update_time timestamp without time zone, -- 修改时间
    kind        character varying(32),       -- 种类
    value       character varying(4096),     -- 值
    label       character varying(4000),     -- 文本
    remark      character varying(255),      -- 备注
    ordering    smallint,                    -- 排序值
    slug        character varying(32)        -- 别名（slug）是网址的唯一标识部分，通常位于 URL 的末尾
);
comment on column system.dict.id is '主键';
comment on column system.dict.create_time is '创建时间';
comment on column system.dict.update_time is '修改时间';
comment on column system.dict.kind is '种类';
comment on column system.dict.value is '值';
comment on column system.dict.ordering is '排序值';
comment on column system.dict.remark is '备注';
comment on column system.dict.label is '文本';
comment on column system.dict.slug is '别名（slug）是网址的唯一标识部分，通常位于 URL 的末尾';

create index dict_kind_idx on system.dict using btree (kind);
create unique index dict_slug_uidx on system.dict using btree (slug);

alter sequence system.dict_id_seq restart with 100001;
-- rollback drop table system.dict;
