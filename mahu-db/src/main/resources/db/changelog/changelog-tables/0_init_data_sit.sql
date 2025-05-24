-- liquibase formatted sql


-- changeset kzou227@qq.com:SIT.202505232109 context:sit
-- comment: SIT专用测试数据
INSERT INTO system.department (id, created_at, updated_at, name, parent_id, leader_id, ordering)
VALUES (200, current_timestamp(0), current_timestamp(0), '测试部', null, 1, 0);
INSERT INTO system.department (id, created_at, updated_at, name, parent_id, leader_id, ordering)
VALUES (201, current_timestamp(0), current_timestamp(0), '测试1部', 200, null, 1);
INSERT INTO system.department (id, created_at, updated_at, name, parent_id, leader_id, ordering)
VALUES (202, current_timestamp(0), current_timestamp(0), '测试2部', 200, null, 2);
--
