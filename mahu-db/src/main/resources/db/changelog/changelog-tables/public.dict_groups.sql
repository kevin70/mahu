-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251125
CREATE TABLE public.dict_groups (
  id VARCHAR(50) NOT NULL CONSTRAINT dict_groups_pk PRIMARY KEY,
  name VARCHAR(255),
  description VARCHAR(4096),
  enabled BOOLEAN,
  visibility SMALLINT,
  value_regex VARCHAR(512),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

COMMENT ON TABLE public.dict_groups IS '字典分组';

COMMENT ON COLUMN public.dict_groups.id IS '字典分组 ID';

COMMENT ON COLUMN public.dict_groups.name IS '字典分组名称';

COMMENT ON COLUMN public.dict_groups.description IS '描述';

COMMENT ON COLUMN public.dict_groups.enabled IS '是否启用';

COMMENT ON COLUMN public.dict_groups.visibility IS '可见性
- 0: 私有的，仅限内部使用
- 1: 公共的
- 2: 受限的';

COMMENT ON COLUMN public.dict_groups.value_regex IS '字典值格式校验正则，由应用层在写入时执行校验';

COMMENT ON COLUMN public.dict_groups.created_at IS '创建时间';

COMMENT ON COLUMN public.dict_groups.updated_at IS '更新时间';

-- rollback drop table public.dict_groups;
;

-- changeset kzou227@qq.com:202603151000
ALTER TABLE public.dict_groups
ADD COLUMN preset BOOLEAN DEFAULT FALSE;

COMMENT ON COLUMN public.dict_groups.preset IS '是否预置';

-- rollback ALTER TABLE public.dict_groups DROP COLUMN preset;
