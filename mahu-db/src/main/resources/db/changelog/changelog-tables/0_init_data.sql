-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251150
-- comment: 测试专用
INSERT INTO
  sys.auth_clients (
    created_at,
    updated_at,
    deleted,
    client_id,
    client_secret,
    remark
  )
VALUES
  (
    CURRENT_TIMESTAMP(0),
    CURRENT_TIMESTAMP(0),
    'F',
    '0KPY6J2C5XP5R',
    'y_6aEKTb_AjOMvQczYY4cwfgRnXvVBCJiPYtmW54HmlHKs7xOS4ZeBfOfK5OmWMnTIztIVnnehD9qspuzUoRXtGj5rNyDq940KtkbiJaI8GNgVaYXpRa9WPpmZBLEX8Eoyl-8vDsgOQOctxieRGJbSmYXmAxMW9t1aqhCo_l459qnINKv8lhj9b-hs3R0EZLZo3VU13aDURSMhF_l4-8rvpOrqzvLxZumK8A3a4dXUwFPI25NGJSaLsGFbqLTBMm',
    '测试专用'
  );

-- rollback delete from sys.auth_clients where client_id='0KPY6J2C5XP5R';
;

-- changeset kzou227@qq.com:202508251151
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

-- changeset kzou227@qq.com:202508251152
-- comment: 系统超级管理员
INSERT INTO
  sys.admins (
    id,
    created_at,
    updated_at,
    deleted,
    username,
    PASSWORD,
    nickname,
    avatar,
    status,
    gender
  )
VALUES
  (
    1,
    CURRENT_TIMESTAMP(0),
    CURRENT_TIMESTAMP(0),
    'F',
    'houge',
    '$argon2id$v=19$m=15360,t=2,p=1$RmT8NvroGRJkFVnmimIxcOM3foB5lPOFQYj5OHhyYSFJf+YByD9nAiIZXOZFiVaLb5YPve4v4lfyJyyuE6+sBg$JCSB3sXOeemq5m6JQ1Uo56+GIwlOvotRR7npwUMznns',
    '超级管理员',
    'https://www.gravatar.com/avatar',
    22,
    0
  );

INSERT INTO
  sys.admin_roles (admin_id, role_id)
VALUES
  (1, 1);

-- rollback delete from sys.admins where id=1;
-- rollback delete from sys.admin_roles where admin_id=1 and role_id=1;
