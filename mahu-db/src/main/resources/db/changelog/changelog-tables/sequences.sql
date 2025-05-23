-- liquibase formatted sql


-- changeset kzou227@qq.com:0KSXJX67E2W5W
create sequence admin_node_seq
    as integer
    cycle;

comment on sequence admin_node_seq is 'Admin 应用实例节点序列，一般用用于生成 TSID 中 node';
-- rollback drop sequence admin_node_seq;
