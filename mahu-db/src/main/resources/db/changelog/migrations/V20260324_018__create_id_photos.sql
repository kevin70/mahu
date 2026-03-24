-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-018
CREATE TABLE sys.id_photos (
  id VARCHAR(50) NOT NULL CONSTRAINT id_photos_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  uid BIGINT NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  status SMALLINT NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  metadata JSONB
);

-- rollback drop table sys.id_photos;
