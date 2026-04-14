-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260414-002 context:sit labels:sit
-- comment: admin_login_logs 测试数据
INSERT INTO
  sys.admin_login_logs (
    id,
    created_at,
    admin_id,
    grant_type,
    client_id,
    username,
    success,
    reason_code,
    reason_detail,
    ip_addr,
    user_agent
  )
SELECT
  (
    '00000000-0000-0000-0000-' || lpad(gs::TEXT, 12, '0')
  )::UUID,
  TIMESTAMPTZ '2026-05-01 09:00:00+08:00' + (gs - 1) * INTERVAL '5 minute',
  CASE
    WHEN gs % 5 = 2 THEN NULL
    WHEN gs % 5 = 4 THEN NULL
    ELSE -1000 - gs
  END,
  CASE
    WHEN gs % 10 = 0 THEN 'REFRESH_TOKEN'
    ELSE 'PASSWORD'
  END,
  '01KKDV0EGWSZ3XW38WV20Q86QW',
  'admin_login_log_test_' || lpad(gs::TEXT, 2, '0'),
  gs % 5 NOT IN (2, 4),
  CASE
    WHEN gs % 5 = 2 THEN 'ADMIN_NOT_FOUND'
    WHEN gs % 5 = 4 AND gs % 10 = 0 THEN 'TOKEN_INVALID'
    WHEN gs % 5 = 4 THEN 'PASSWORD_MISMATCH'
    ELSE NULL
  END,
  CASE
    WHEN gs % 5 = 2 THEN '测试管理员不存在'
    WHEN gs % 5 = 4 AND gs % 10 = 0 THEN '测试刷新令牌无效'
    WHEN gs % 5 = 4 THEN '测试密码不匹配'
    ELSE NULL
  END,
  '10.10.0.' || ((gs - 1) % 20 + 1),
  'mahu-test-agent/' || lpad(gs::TEXT, 2, '0')
FROM
  generate_series(1, 50) AS gs;

-- rollback delete from sys.admin_login_logs where username like 'admin_login_log_test_%';
;
