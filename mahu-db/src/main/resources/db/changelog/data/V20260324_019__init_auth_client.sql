-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-019
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
