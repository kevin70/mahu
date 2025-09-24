-- liquibase formatted sql
-- changeset kzou227@qq.com:202509172106
CREATE TABLE
  public.auth_client (
    client_id VARCHAR(50) NOT NULL CONSTRAINT auth_client_pk PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted "char" DEFAULT 'F'::"char",
    client_secret VARCHAR(4000),
    remark VARCHAR(500),
    LABEL VARCHAR(128),
    terminal_type VARCHAR(32),
    wechat_appid VARCHAR(128),
    wechat_appsecret VARCHAR(512)
  );

COMMENT ON TABLE public.auth_client IS '认证客户端';

COMMENT ON COLUMN public.auth_client.client_id IS '客户端 ID';

COMMENT ON COLUMN public.auth_client.created_at IS '创建时间';

COMMENT ON COLUMN public.auth_client.updated_at IS '修改时间';

COMMENT ON COLUMN public.auth_client.deleted IS '软删除标识';

COMMENT ON COLUMN public.auth_client.client_secret IS '客户端密钥';

COMMENT ON COLUMN public.auth_client.remark IS '备注';

COMMENT ON COLUMN public.auth_client.label IS '标签';

COMMENT ON COLUMN public.auth_client.terminal_type IS '终端类型';

COMMENT ON COLUMN public.auth_client.wechat_appid IS '微信应用 ID';

COMMENT ON COLUMN public.auth_client.wechat_appsecret IS '微信应用密钥';

-- rollback drop table public.auth_client;
