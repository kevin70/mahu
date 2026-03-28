-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-015
CREATE TABLE sys.feature_flags (
  id INTEGER NOT NULL CONSTRAINT feature_flags_pk PRIMARY KEY,
  code VARCHAR(128) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  description TEXT,
  enabled BOOLEAN DEFAULT FALSE NOT NULL,
  preset BOOLEAN DEFAULT FALSE NOT NULL,
  enable_at TIMESTAMPTZ,
  disable_at TIMESTAMPTZ,
  ordering SMALLINT DEFAULT 0 NOT NULL,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ
);

CREATE INDEX idx_feature_flags_schedule ON sys.feature_flags (enable_at, disable_at)
WHERE
  (
    (enable_at IS NOT NULL)
    OR (disable_at IS NOT NULL)
  );

-- rollback drop table sys.feature_flags;
