-- liquibase formatted sql


-- changeset kzou227@qq.com:202403201454
create table system.dicts
(
    id          serial
        primary key,
    create_time timestamp,
    update_time timestamp,
    name        varchar(32),
    value       varchar(4096),
    ordering    smallint,
    remark      varchar(255)
);

comment
on column system.dicts.id is '主键';

comment
on column system.dicts.create_time is '创建时间';

comment
on column system.dicts.update_time is '修改时间';

comment
on column system.dicts.name is '字典名';

comment
on column system.dicts.value is '字典值';

comment
on column system.dicts.ordering is '排序值';

comment
on column system.dicts.remark is '备注';

alter sequence system.dicts_id_seq restart with 100001;
-- rollback drop table system.dicts;
