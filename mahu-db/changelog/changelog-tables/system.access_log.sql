-- liquibase formatted sql


-- changeset kzou227@qq.com:202412010008
create table system.access_log
(
    id              bigserial primary key,       -- 主键
    create_time     timestamp without time zone, -- 创建时间
    employee_id     bigint,                      -- 访问职员 ID
    ip_addr         character varying(64),       -- 访问 IP
    trace_id        character varying(128),      -- 追踪 ID
    channel_id      character varying(128),      -- 客户端/服务器连接套接字的标识（不保证唯一）
    method          character varying(16),       -- 请求方法
    uri_path        character varying(2048),     -- 请求路径
    uri_query       character varying(2048),     -- 请求查询参数
    referer         character varying(2048),     -- HTTP referer
    protocol        character varying(1024),     -- 访问协议
    user_agent      character varying(1024),     -- UA
    response_status smallint,                    -- 响应状态
    response_bytes  bigint                       -- 响应字节数
);
comment on column system.access_log.id is '主键';
comment on column system.access_log.create_time is '创建时间';
comment on column system.access_log.employee_id is '访问职员 ID';
comment on column system.access_log.ip_addr is '访问 IP';
comment on column system.access_log.trace_id is '追踪 ID';
comment on column system.access_log.channel_id is '客户端/服务器连接套接字的标识（不保证唯一）';
comment on column system.access_log.method is '请求方法';
comment on column system.access_log.uri_path is '请求路径';
comment on column system.access_log.uri_query is '请求查询参数';
comment on column system.access_log.referer is 'HTTP referer';
comment on column system.access_log.protocol is '访问协议';
comment on column system.access_log.response_status is '响应状态';
comment on column system.access_log.response_bytes is '响应字节数';
comment on column system.access_log.user_agent is 'UA';

create index access_log_create_time_idx on system.access_log using btree (create_time);
create index access_log_user_id_idx on system.access_log using btree (employee_id);
-- rollback drop table system.access_log;
