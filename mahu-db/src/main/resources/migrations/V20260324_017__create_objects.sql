-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-017
CREATE TABLE sys.objects (
  id VARCHAR(50) NOT NULL CONSTRAINT objects_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  uid BIGINT,
  type SMALLINT DEFAULT 0 NOT NULL,
  status SMALLINT NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  metadata JSONB
);

-- rollback drop table sys.objects;
