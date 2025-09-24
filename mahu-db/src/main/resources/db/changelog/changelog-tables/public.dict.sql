--
-- liquibase formatted sql
-- changeset kzou227@qq.com:202508251125
CREATE TABLE
  public.dict_type (
    id VARCHAR(50) NOT NULL CONSTRAINT dict_type_pk PRIMARY KEY,
    NAME VARCHAR(255),
    description VARCHAR(4096),
    disabled "char",
    created_at TIMESTAMP,
    updated_at TIMESTAMP
  );

COMMENT ON TABLE public.dict_type IS '字典类型';

COMMENT ON COLUMN public.dict_type.id IS '字典类型 ID';

COMMENT ON COLUMN public.dict_type.name IS '字典类型名称';

COMMENT ON COLUMN public.dict_type.disabled IS '状态（T:禁用, F:启用）';

COMMENT ON COLUMN public.dict_type.created_at IS '创建时间';

COMMENT ON COLUMN public.dict_type.updated_at IS '更新时间';

COMMENT ON COLUMN public.dict_type.description IS '描述';

-- rollback drop table public.dict_type;
--
-- liquibase formatted sql
-- changeset kzou227@qq.com:202508251126
CREATE TABLE
  public.dict (
    dc serial NOT NULL CONSTRAINT dict_pk PRIMARY KEY,
    LABEL VARCHAR(255),
    VALUE VARCHAR(4096),
    disabled "char",
    ordering INTEGER,
    dict_type_id VARCHAR(50) NOT NULL,
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
  1001;

-- rollback drop table public.dict;
