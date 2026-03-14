-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251110
CREATE TABLE sys.id_photos (
  id VARCHAR(50) NOT NULL CONSTRAINT id_photos_pk PRIMARY KEY,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  uid BIGINT NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  status SMALLINT NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  metadata JSONB
);

COMMENT ON COLUMN sys.id_photos.id IS '主键';

COMMENT ON COLUMN sys.id_photos.created_at IS '创建时间';

COMMENT ON COLUMN sys.id_photos.updated_at IS '更新时间';

COMMENT ON COLUMN sys.id_photos.uid IS '用户 ID';

COMMENT ON COLUMN sys.id_photos.type IS '证件类型
- 0：证件照片
- 1：身份证头像面
- 2：身份证国徽面
- 3：护照';

COMMENT ON COLUMN sys.id_photos.status IS '状态';

COMMENT ON COLUMN sys.id_photos.object_key IS '对象的完整 key（含前缀）';

COMMENT ON COLUMN sys.id_photos.metadata IS '可以用来存储额外的元数据，比如图片尺寸、背景颜色、格式等。 ';

-- rollback drop table sys.id_photos;
