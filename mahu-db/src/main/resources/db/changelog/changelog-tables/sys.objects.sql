-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251111
CREATE TABLE sys.objects (
  id VARCHAR(50) NOT NULL CONSTRAINT objects_pk PRIMARY KEY,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  uid BIGINT,
  type SMALLINT DEFAULT 0 NOT NULL,
  status SMALLINT NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  metadata JSONB
);

COMMENT ON TABLE sys.objects IS 'OSS对象存储';

COMMENT ON COLUMN sys.objects.id IS '主键';

COMMENT ON COLUMN sys.objects.created_at IS '创建时间';

COMMENT ON COLUMN sys.objects.updated_at IS '更新时间';

COMMENT ON COLUMN sys.objects.uid IS '用户 ID';

COMMENT ON COLUMN sys.objects.type IS '资源类型';

COMMENT ON COLUMN sys.objects.status IS '状态';

COMMENT ON COLUMN sys.objects.object_key IS '对象的完整 key（含前缀）';

COMMENT ON COLUMN sys.objects.metadata IS '可以用来存储额外的元数据，比如图片尺寸、背景颜色、格式等。 ';

-- rollback drop table sys.objects;
