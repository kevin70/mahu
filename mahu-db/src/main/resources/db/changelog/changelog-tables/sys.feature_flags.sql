-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202603141200
CREATE TABLE sys.feature_flags (
  id INTEGER NOT NULL CONSTRAINT feature_flags_pk PRIMARY KEY,
  code VARCHAR(128) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  description TEXT,
  enabled BOOLEAN DEFAULT FALSE NOT NULL,
  preset BOOLEAN DEFAULT FALSE NOT NULL,
  enable_at TIMESTAMPTZ,
  disable_at TIMESTAMPTZ,
  ordering SMALLINT DEFAULT 0 NOT NULL,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ
);

CREATE INDEX idx_feature_flags_schedule ON sys.feature_flags (enable_at, disable_at)
WHERE
  (
    (enable_at IS NOT NULL)
    OR (disable_at IS NOT NULL)
  );

COMMENT ON TABLE sys.feature_flags IS '功能开关主表';

COMMENT ON COLUMN sys.feature_flags.id IS '主键，自增';

COMMENT ON COLUMN sys.feature_flags.code IS '全局唯一标识，程序通过此字段读取开关；命名规范：{module}.{feature}，如 payment.wechat';

COMMENT ON COLUMN sys.feature_flags.name IS '可读名称，如\"微信支付\"';

COMMENT ON COLUMN sys.feature_flags.description IS '开关用途说明，建议描述：功能用途 + 关闭后的影响';

COMMENT ON COLUMN sys.feature_flags.enabled IS '开关状态：true=开启，false=关闭，修改后直接生效';

COMMENT ON COLUMN sys.feature_flags.preset IS '系统预置标志：true=内置开关，禁止删除';

COMMENT ON COLUMN sys.feature_flags.enable_at IS '定时开启时间，NULL=不启用定时；定时任务到期后置 enabled=true 并清空此字段';

COMMENT ON COLUMN sys.feature_flags.disable_at IS '定时关闭时间，NULL=不启用定时；定时任务到期后置 enabled=false 并清空此字段';

COMMENT ON COLUMN sys.feature_flags.ordering IS '排序权重，值越大越靠前，后台管理页展示顺序依据';

COMMENT ON COLUMN sys.feature_flags.created_at IS '创建时间';

COMMENT ON COLUMN sys.feature_flags.updated_at IS '更新时间';

COMMENT ON INDEX sys.idx_feature_flags_schedule IS '定时任务专用，仅索引配置了定时控制的行';

-- rollback drop table sys.feature_flags;
