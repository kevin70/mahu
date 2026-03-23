-- liquibase formatted sql
;

-- changeset kzou227@qq.com:202508251150
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

-- changeset kzou227@qq.com:202508251151
-- comment: 系统超级管理员
INSERT INTO
  sys.roles (
    id,
    created_at,
    updated_at,
    deleted,
    NAME,
    remark,
    ordering,
    permissions
  )
VALUES
  (
    1,
    CURRENT_TIMESTAMP(0),
    CURRENT_TIMESTAMP(0),
    'F',
    '超级管理员',
    '系统超级管理员，拥有系统所有权限',
    9999,
    '["*"]'::JSONB
  );

-- rollback delete from sys.roles where id=1;
;

-- changeset kzou227@qq.com:202508251152
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
    22,
    0
  );

INSERT INTO
  sys.admin_roles (admin_id, role_id)
VALUES
  (1, 1);

-- rollback delete from sys.admins where id=1;
-- rollback delete from sys.admin_roles where admin_id=1 and role_id=1;
;

-- changeset kzou227@qq.com:202603231200
-- comment: 字典组与字典项（性别、终端类型、颜色、通用状态、管理员状态、延时任务状态）
INSERT INTO
  public.dict_groups (
    id,
    name,
    description,
    enabled,
    visibility,
    created_at,
    updated_at,
    preset
  )
VALUES
  (
    'gender',
    '性别',
    '性别',
    TRUE,
    1,
    '2026-03-05 14:41:01.000000',
    '2026-03-05 14:41:03.000000',
    TRUE
  );

INSERT INTO
  public.dict_groups (
    id,
    name,
    description,
    enabled,
    visibility,
    created_at,
    updated_at,
    preset
  )
VALUES
  (
    'auth_terminal_type',
    '终端类型',
    '终端类型',
    TRUE,
    1,
    '2026-03-21 22:38:45.000000',
    '2026-03-22 12:20:27.840000',
    TRUE
  );

INSERT INTO
  public.dict_groups (
    id,
    name,
    description,
    enabled,
    visibility,
    created_at,
    updated_at,
    preset
  )
VALUES
  (
    'dict_color',
    '颜色',
    '字典项可选颜色',
    TRUE,
    1,
    '2026-03-23 13:44:16.000000',
    '2026-03-23 13:44:18.000000',
    TRUE
  );

INSERT INTO
  public.dict_groups (
    id,
    name,
    description,
    enabled,
    visibility,
    created_at,
    updated_at,
    preset
  )
VALUES
  (
    'common_enabled_status',
    '启用/禁用',
    '通用状态',
    TRUE,
    1,
    '2026-03-22 00:04:19.000000',
    '2026-03-23 15:14:58.274000',
    TRUE
  );

INSERT INTO
  public.dict_groups (
    id,
    name,
    description,
    enabled,
    visibility,
    created_at,
    updated_at,
    preset
  )
VALUES
  (
    'admin_status',
    '管理员状态',
    '管理员状态',
    TRUE,
    1,
    '2026-03-22 00:05:11.000000',
    '2026-03-23 14:40:24.689000',
    TRUE
  );

INSERT INTO
  public.dict_groups (
    id,
    name,
    description,
    enabled,
    visibility,
    created_at,
    updated_at,
    preset
  )
VALUES
  (
    'delayed_task_status',
    '延时任务状态',
    '延时任务',
    TRUE,
    1,
    '2026-03-23 15:17:16.000000',
    '2026-03-23 15:36:32.373000',
    FALSE
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    100,
    '女',
    '2',
    TRUE,
    1,
    'gender',
    '2026-03-05 14:41:36.000000',
    '2026-03-05 14:41:37.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    101,
    '男',
    '1',
    TRUE,
    2,
    'gender',
    '2026-03-05 14:41:24.000000',
    '2026-03-05 14:41:24.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    110,
    '启用',
    'true',
    TRUE,
    3,
    'common_enabled_status',
    '2026-03-23 11:20:46.000000',
    '2026-03-23 15:14:58.304000',
    TRUE,
    'success'
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    111,
    '禁用',
    'false',
    TRUE,
    2,
    'common_enabled_status',
    '2026-03-23 11:21:26.000000',
    '2026-03-23 15:14:58.304000',
    TRUE,
    'error'
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    200,
    '主要',
    'primary',
    TRUE,
    99,
    'dict_color',
    '2026-03-23 13:45:19.000000',
    '2026-03-23 13:45:21.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    201,
    '次要',
    'secondary',
    TRUE,
    98,
    'dict_color',
    '2026-03-23 13:46:46.000000',
    '2026-03-23 13:46:47.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    202,
    '成功',
    'success',
    TRUE,
    97,
    'dict_color',
    '2026-03-23 13:47:23.000000',
    '2026-03-23 13:47:24.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    203,
    '信息',
    'info',
    TRUE,
    96,
    'dict_color',
    '2026-03-23 13:47:47.000000',
    '2026-03-23 13:47:48.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    204,
    '警告',
    'warning',
    TRUE,
    95,
    'dict_color',
    '2026-03-23 13:48:14.000000',
    '2026-03-23 13:48:15.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    205,
    '中性',
    'neutral',
    TRUE,
    94,
    'dict_color',
    '2026-03-23 13:48:39.000000',
    '2026-03-23 13:48:40.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    206,
    '错误',
    'error',
    TRUE,
    93,
    'dict_color',
    '2026-03-23 13:49:01.000000',
    '2026-03-23 13:49:02.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    1000,
    '微信小程序',
    'WECHAT_XCX',
    TRUE,
    1,
    'auth_terminal_type',
    '2026-03-21 22:39:45.000000',
    '2026-03-22 12:20:42.074000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    1001,
    '浏览器',
    'BROWSER',
    TRUE,
    2,
    'auth_terminal_type',
    '2026-03-21 22:40:34.000000',
    '2026-03-21 22:40:36.000000',
    TRUE,
    NULL
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    1100,
    '活跃',
    '22',
    TRUE,
    9,
    'admin_status',
    '2026-03-23 11:22:39.000000',
    '2026-03-23 14:40:24.787000',
    TRUE,
    'success'
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    1101,
    '禁用',
    '74',
    TRUE,
    2,
    'admin_status',
    '2026-03-23 11:23:22.000000',
    '2026-03-23 14:40:24.797000',
    TRUE,
    'error'
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    1201,
    '待执行',
    '11',
    TRUE,
    1,
    'delayed_task_status',
    '2026-03-23 15:29:16.000000',
    '2026-03-23 15:36:32.392000',
    TRUE,
    'warning'
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    1202,
    '执行中',
    '16',
    TRUE,
    2,
    'delayed_task_status',
    '2026-03-23 15:30:06.000000',
    '2026-03-23 15:36:32.392000',
    TRUE,
    'info'
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    1203,
    '完成',
    '88',
    TRUE,
    3,
    'delayed_task_status',
    '2026-03-23 15:30:40.000000',
    '2026-03-23 15:36:32.392000',
    TRUE,
    'primary'
  );

INSERT INTO
  public.dicts (
    dc,
    label,
    value,
    enabled,
    ordering,
    group_id,
    created_at,
    updated_at,
    preset,
    color
  )
VALUES
  (
    1204,
    '归档',
    '97',
    TRUE,
    4,
    'delayed_task_status',
    '2026-03-23 15:31:21.000000',
    '2026-03-23 15:36:32.391000',
    TRUE,
    'neutral'
  );

-- rollback delete from public.dicts where dc in (100, 101, 110, 111, 200, 201, 202, 203, 204, 205, 206, 1000, 1001, 1100, 1101, 1201, 1202, 1203, 1204);
-- rollback delete from public.dict_groups where id in ('gender', 'auth_terminal_type', 'dict_color', 'common_enabled_status', 'admin_status', 'delayed_task_status');
;
