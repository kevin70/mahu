-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202511241532
CREATE TABLE sys.id_photo (
  id VARCHAR(50) NOT NULL CONSTRAINT id_photo_pk PRIMARY KEY,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  uid BIGINT NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  status SMALLINT NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  metadata JSONB
);

comment ON COLUMN sys.id_photo.id IS '主键';

comment ON COLUMN sys.id_photo.created_at IS '创建时间';

comment ON COLUMN sys.id_photo.updated_at IS '更新时间';

comment ON COLUMN sys.id_photo.uid IS '用户 ID';

comment ON COLUMN sys.id_photo.type IS '证件类型
- 0：证件照片
- 1：身份证头像面
- 2：身份证国徽面
- 3：护照';

comment ON COLUMN sys.id_photo.status IS '状态';

comment ON COLUMN sys.id_photo.object_key IS '对象的完整 key（含前缀）';

comment ON COLUMN sys.id_photo.metadata IS '可以用来存储额外的元数据，比如图片尺寸、背景颜色、格式等。 ';

-- rollback drop table sys.id_photo;
