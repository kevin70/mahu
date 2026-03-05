-- liquibase formatted sql
;

CREATE TABLE sys.features (
  id INTEGER NOT NULL CONSTRAINT features_pk PRIMARY KEY,
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
  properties JSONB,
  properties_schema JSONB
);

CREATE UNIQUE INDEX features_code_uidx ON sys.features (code);

CREATE INDEX features_module_idx ON sys.features (module);

CREATE INDEX features_status_idx ON sys.features (status);

comment ON COLUMN sys.features.id IS '主键';

comment ON COLUMN sys.features.created_at IS '创建时间';

comment ON COLUMN sys.features.updated_at IS '更新时间';

comment ON COLUMN sys.features.module IS '模块';

comment ON COLUMN sys.features.code IS '功能代码';

comment ON COLUMN sys.features.name IS '名称';

comment ON COLUMN sys.features.description IS '描述';

comment ON COLUMN sys.features.status IS '状态';

comment ON COLUMN sys.features.effective_from IS '生效开始时间（精确到秒）';

comment ON COLUMN sys.features.effective_to IS '生效结束时间（精确到秒）';

comment ON COLUMN sys.features.start_time IS '每天的开始时间（精确到秒）';

comment ON COLUMN sys.features.end_time IS '每天的结束时间（精确到秒，结束时间小于开始时间代表跨天）';

comment ON COLUMN sys.features.weekdays IS '启用的星期，数字数组，1=周一 ~ 7=周日，例如 [1,2,3,4,5] 表示工作日';

comment ON COLUMN sys.features.allow_user_rb IS '可用的用户（Roaring Bitmap 编码的 user_id 集合）';

comment ON COLUMN sys.features.deny_user_rb IS '禁用的用户（Roaring Bitmap 编码的 user_id 集合）';

comment ON COLUMN sys.features.properties IS '扩展属性';

comment ON COLUMN sys.features.properties_schema IS '扩展属性的 JSON Schema 定义';

-- rollback drop table sys.features;
