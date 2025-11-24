-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202511241531
CREATE TABLE sys.asset (
  id bigserial CONSTRAINT asset_pk PRIMARY KEY,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  uid BIGINT NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  status SMALLINT NOT NULL,
  object_name VARCHAR(255) NOT NULL,
  metadata JSONB
);

comment ON TABLE sys.asset IS '资源表';

comment ON COLUMN sys.asset.id IS '主键';

comment ON COLUMN sys.asset.created_at IS '创建时间';

comment ON COLUMN sys.asset.updated_at IS '更新时间';

comment ON COLUMN sys.asset.uid IS '用户 ID';

comment ON COLUMN sys.asset.type IS '资源类型';

comment ON COLUMN sys.asset.status IS '状态';

comment ON COLUMN sys.asset.object_name IS 'OSS 对象名称';

comment ON COLUMN sys.asset.metadata IS '可以用来存储额外的元数据，比如图片尺寸、背景颜色、格式等。 ';

ALTER SEQUENCE sys.asset_id_seq RESTART
WITH
  100001;

-- rollback drop table sys.asset;
