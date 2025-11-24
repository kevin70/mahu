-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202511232228
CREATE TABLE sys.feature (
  id INTEGER NOT NULL CONSTRAINT feature_pk PRIMARY KEY,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  module VARCHAR(50),
  code VARCHAR(50),
  name VARCHAR(100),
  description VARCHAR(1024),
  status SMALLINT,
  effective_from TIMESTAMP,
  effective_to TIMESTAMP,
  start_time TIME,
  end_time TIME,
  weekdays JSONB,
  allow_user_rb bytea,
  deny_user_rb bytea,
  extra_properties JSONB,
  extra_schema JSONB
);

comment ON COLUMN sys.feature.id IS '主键';

comment ON COLUMN sys.feature.created_at IS '创建时间';

comment ON COLUMN sys.feature.updated_at IS '更新时间';

comment ON COLUMN sys.feature.module IS '模块';

comment ON COLUMN sys.feature.code IS '功能代码';

comment ON COLUMN sys.feature.name IS '名称';

comment ON COLUMN sys.feature.description IS '描述';

comment ON COLUMN sys.feature.status IS '状态';

comment ON COLUMN sys.feature.effective_from IS '生效开始时间（精确到秒）';

comment ON COLUMN sys.feature.effective_to IS '生效结束时间（精确到秒）';

comment ON COLUMN sys.feature.start_time IS '每天的开始时间（精确到秒）';

comment ON COLUMN sys.feature.end_time IS '每天的结束时间（精确到秒，结束时间小于开始时间代表跨天）';

comment ON COLUMN sys.feature.weekdays IS '启用的星期';

comment ON COLUMN sys.feature.allow_user_rb IS '可用的用户';

comment ON COLUMN sys.feature.deny_user_rb IS '禁用的用户';

comment ON COLUMN sys.feature.extra_properties IS '扩展属性';

comment ON COLUMN sys.feature.extra_schema IS 'JSON Schema';

-- rollback drop table sys.feature;
