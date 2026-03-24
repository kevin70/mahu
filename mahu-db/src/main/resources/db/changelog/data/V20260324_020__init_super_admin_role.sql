-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-020
-- comment: 系统超级管理员
INSERT INTO
  sys.roles (
    id,
    created_at,
    updated_at,
    deleted,
    NAME,
    remark,
    ordering,
    permissions
  )
VALUES
  (
    1,
    CURRENT_TIMESTAMP(0),
    CURRENT_TIMESTAMP(0),
    'F',
    '超级管理员',
    '系统超级管理员，拥有系统所有权限',
    9999,
    '["*"]'::JSONB
  );

-- rollback delete from sys.roles where id=1;
;
