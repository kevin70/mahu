-- liquibase formatted sql
-- changeset kzou227@qq.com:202508242205
CREATE SEQUENCE admin_node_seq AS INTEGER CYCLE;

COMMENT ON SEQUENCE admin_node_seq IS 'Admin 应用实例节点序列，一般用用于生成 TSID 中 node';

-- rollback drop sequence admin_node_seq;
-- changeset kzou227@qq.com:202508242206
CREATE SEQUENCE task_node_seq AS INTEGER CYCLE;

COMMENT ON SEQUENCE task_node_seq IS 'Task 应用实例节点序列，一般用用于生成 TSID 中 node';

-- rollback drop sequence task_node_seq;
