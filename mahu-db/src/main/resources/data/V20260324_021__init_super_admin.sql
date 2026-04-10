-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-021
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
    200,
    0
  );

INSERT INTO
  sys.admin_roles (admin_id, role_id)
VALUES
  (1, 1);

-- rollback delete from sys.admins where id=1;
-- rollback delete from sys.admin_roles where admin_id=1 and role_id=1;
;
