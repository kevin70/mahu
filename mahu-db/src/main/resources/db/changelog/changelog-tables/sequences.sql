-- liquibase formatted sql


-- changeset kzou227@qq.com:202508242205
create sequence admin_node_seq
    as integer
    cycle;

comment
on sequence admin_node_seq is 'Admin 应用实例节点序列，一般用用于生成 TSID 中 node';
-- rollback drop sequence admin_node_seq;


-- changeset kzou227@qq.com:202508242206
create sequence task_node_seq
    as integer
    cycle;

comment
on sequence task_node_seq is 'Task 应用实例节点序列，一般用用于生成 TSID 中 node';
-- rollback drop sequence task_node_seq;
