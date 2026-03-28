-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-004
CREATE TABLE public.dicts (
  dc serial CONSTRAINT dicts_pk PRIMARY KEY,
  label VARCHAR(255),
  value VARCHAR(1024),
  enabled BOOLEAN,
  ordering INTEGER,
  color VARCHAR(64),
  preset BOOLEAN NOT NULL DEFAULT FALSE,
  group_id VARCHAR(50) NOT NULL,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ
);

COMMENT ON TABLE public.dicts IS '字典数据';

COMMENT ON COLUMN public.dicts.dc IS '字典代码';

COMMENT ON COLUMN public.dicts.label IS '字典数据文本';

COMMENT ON COLUMN public.dicts.value IS '字典数据值';

COMMENT ON COLUMN public.dicts.enabled IS '是否启用';

COMMENT ON COLUMN public.dicts.ordering IS '排序值';

COMMENT ON COLUMN public.dicts.color IS '展示用颜色（如 CSS 色值）';

COMMENT ON COLUMN public.dicts.preset IS '是否预置';

COMMENT ON COLUMN public.dicts.group_id IS '字典分组代码';

COMMENT ON COLUMN public.dicts.created_at IS '创建时间';

COMMENT ON COLUMN public.dicts.updated_at IS '更新时间';

CREATE UNIQUE INDEX dicts_group_id_value_uidx ON public.dicts (group_id, value);

ALTER SEQUENCE public.dicts_dc_seq RESTART
WITH
  100001;

-- rollback drop table public.dicts;
