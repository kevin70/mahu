-- liquibase formatted sql
;

-- changeset kzou227@qq.com:20260324-016
CREATE TABLE sys.delayed_tasks (
  id UUID NOT NULL CONSTRAINT delayed_tasks_pk PRIMARY KEY,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  topic VARCHAR(50),
  status SMALLINT,
  delay_until TIMESTAMPTZ,
  attempts INTEGER,
  max_attempts SMALLINT,
  lock_at TIMESTAMPTZ,
  lease_seconds SMALLINT,
  reference_id VARCHAR(128) NOT NULL,
  payload JSONB,
  idempotency_key VARCHAR(128)
);

CREATE UNIQUE INDEX delayed_tasks_topic_idempotency_key_uidx ON sys.delayed_tasks (topic, idempotency_key);

CREATE INDEX delayed_tasks_delay_until_idx ON sys.delayed_tasks (delay_until)
WHERE
  (status = 130);

CREATE INDEX delayed_tasks_lock_at_idx ON sys.delayed_tasks (lock_at)
WHERE
  (status = 220);

-- rollback drop table sys.delayed_tasks;
