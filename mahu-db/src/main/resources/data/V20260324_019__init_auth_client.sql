-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-019
-- comment: 测试专用
INSERT INTO
  sys.auth_clients (
    created_at,
    updated_at,
    deleted,
    client_id,
    remark
  )
VALUES
  (
    CURRENT_TIMESTAMP(0),
    CURRENT_TIMESTAMP(0),
    'F',
    '01KKDV0EGWSZ3XW38WV20Q86QW',
    '测试专用'
  );

-- rollback delete from sys.auth_clients where client_id='01KKDV0EGWSZ3XW38WV20Q86QW';
;
