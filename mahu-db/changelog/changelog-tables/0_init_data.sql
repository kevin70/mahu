-- liquibase formatted sql


-- changeset kzou227@qq.com:0KPY6J2C5XP5R
-- comment: 测试专用
INSERT INTO system.client (created_at, updated_at, deleted, client_id, client_secret, remark)
VALUES (current_timestamp, current_timestamp, 'F',
        '0KPY6J2C5XP5R',
        'y_6aEKTb_AjOMvQczYY4cwfgRnXvVBCJiPYtmW54HmlHKs7xOS4ZeBfOfK5OmWMnTIztIVnnehD9qspuzUoRXtGj5rNyDq940KtkbiJaI8GNgVaYXpRa9WPpmZBLEX8Eoyl-8vDsgOQOctxieRGJbSmYXmAxMW9t1aqhCo_l459qnINKv8lhj9b-hs3R0EZLZo3VU13aDURSMhF_l4-8rvpOrqzvLxZumK8A3a4dXUwFPI25NGJSaLsGFbqLTBMm',
        '测试专用');
-- rollback delete from system.client where client_id='0KPY6J2C5XP5R';


-- changeset kzou227@qq.com:0KPY6J2C5XP5P
-- comment: 系统超级管理员
INSERT INTO system.role (id, created_at, updated_at, deleted, name, remark,
                         ordering, permits)
VALUES (1, current_timestamp, current_timestamp, 'F', '超级管理员',
        '系统超级管理员，拥有系统所有权限', 9999, '{*}');
-- rollback delete from system.role where id=1;


-- changeset kzou227@qq.com:0KPYK5KHDXP65
-- comment: 系统超级管理员
INSERT INTO system.admin (id, created_at, updated_at, deleted, username, password, nickname, avatar,
                          status)
VALUES (1, current_timestamp, current_timestamp, 'F', 'houge',
        '$argon2id$v=19$m=15360,t=2,p=1$RmT8NvroGRJkFVnmimIxcOM3foB5lPOFQYj5OHhyYSFJf+YByD9nAiIZXOZFiVaLb5YPve4v4lfyJyyuE6+sBg$JCSB3sXOeemq5m6JQ1Uo56+GIwlOvotRR7npwUMznns',
        '超级管理员',
        'https://www.gravatar.com/avatar',
        1);
-- rollback delete from system.admin where id=1;


-- changeset kzou227@qq.com:0KPYK5KHDXP66
-- comment: 系统超级管理员
INSERT INTO system.admin_role (admin_id, role_id)
VALUES (1, 1);
-- rollback delete from system.admin_role where admin_id=1 and role_id=1;
