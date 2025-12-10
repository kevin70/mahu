-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202511241531
CREATE TABLE sys.object (
  id bigserial CONSTRAINT object_pk PRIMARY KEY,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  uid BIGINT NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  status SMALLINT NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  metadata JSONB
);

comment ON TABLE sys.object IS 'OSS对象存储';

comment ON COLUMN sys.object.id IS '主键';

comment ON COLUMN sys.object.created_at IS '创建时间';

comment ON COLUMN sys.object.updated_at IS '更新时间';

comment ON COLUMN sys.object.uid IS '用户 ID';

comment ON COLUMN sys.object.type IS '资源类型';

comment ON COLUMN sys.object.status IS '状态';

comment ON COLUMN sys.object.object_key IS '对象的完整 key（含前缀）';

comment ON COLUMN sys.object.metadata IS '可以用来存储额外的元数据，比如图片尺寸、背景颜色、格式等。 ';

ALTER SEQUENCE sys.object_id_seq RESTART
WITH
  1000001;

-- rollback drop table sys.object;
