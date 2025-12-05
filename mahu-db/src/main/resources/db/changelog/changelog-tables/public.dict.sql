-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251125
CREATE TABLE dict_type (
  id INTEGER NOT NULL CONSTRAINT dict_type_pk PRIMARY KEY,
  name VARCHAR(255),
  description VARCHAR(4096),
  disabled "char",
  visibility SMALLINT,
  value_regex VARCHAR(512),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

comment ON TABLE dict_type IS '字典类型';

comment ON COLUMN dict_type.id IS '字典类型 ID';

comment ON COLUMN dict_type.name IS '字典类型名称';

comment ON COLUMN dict_type.description IS '描述';

comment ON COLUMN dict_type.disabled IS '状态（T:禁用, F:启用）';

comment ON COLUMN dict_type.visibility IS '可见性
- 0: 私有的，仅限内部使用
- 1: 公共的
- 2: 受限的';

comment ON COLUMN dict_type.value_regex IS '值正则表达式规则';

comment ON COLUMN dict_type.created_at IS '创建时间';

comment ON COLUMN dict_type.updated_at IS '更新时间';

-- rollback drop table public.dict_type;
;

-- changeset kzou227@qq.com:202508251126
CREATE TABLE public.dict (
  dc serial NOT NULL CONSTRAINT dict_pk PRIMARY KEY,
  label VARCHAR(255),
  value VARCHAR(4096),
  disabled "char",
  ordering INTEGER,
  dict_type_id INTEGER NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

COMMENT ON TABLE public.dict IS '字典数据';

COMMENT ON COLUMN public.dict.dc IS '字典代码';

COMMENT ON COLUMN public.dict.dict_type_id IS '字典类型代码';

COMMENT ON COLUMN public.dict.label IS '字典数据文本';

COMMENT ON COLUMN public.dict.value IS '字典数据值';

COMMENT ON COLUMN public.dict.ordering IS '排序值';

COMMENT ON COLUMN public.dict.disabled IS '状态（T:禁用, F:启用）';

COMMENT ON COLUMN public.dict.created_at IS '创建时间';

COMMENT ON COLUMN public.dict.updated_at IS '更新时间';

CREATE INDEX dict_type_i ON public.dict (dict_type_id);

ALTER SEQUENCE public.dict_dc_seq RESTART
WITH
  100001;

-- rollback drop table public.dict;
