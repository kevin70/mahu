-- liquibase formatted sql


-- changeset kzou227@qq.com:202403201506
create table system.secrets
(
    id          serial
        primary key,
    create_time timestamp,
    update_time timestamp,
    deleted     "char",
    name        varchar(40)
        unique,
    str_data    varchar(4000),
    bin_data    bytea
);

comment
on table system.secrets is '安全配置项';

comment
on column system.secrets.create_time is '创建时间';

comment
on column system.secrets.update_time is '修改时间';

comment
on column system.secrets.deleted is '软删除标识';

comment
on column system.secrets.name is '名称';

comment
on column system.secrets.str_data is '文本值';

comment
on column system.secrets.bin_data is '二进制数据';

alter sequence system.secrets_id_seq restart with 100001;
-- rollback drop table system.secrets;
