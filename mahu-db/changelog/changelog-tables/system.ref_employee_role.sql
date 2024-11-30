-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211334
create table system.ref_employee_role
(
    employee_id integer not null, -- 用户ID
    role_id     integer not null  -- 角色ID
);
comment on column system.ref_employee_role.employee_id is '用户ID';
comment on column system.ref_employee_role.role_id is '角色ID';

create unique index ref_employee_role_employee_id_role_id_uidx on system.ref_employee_role using btree (employee_id, role_id);
-- rollback drop table system.ref_employee_role;


-- changeset kzou227@qq.com:202403211357
INSERT INTO system.ref_employee_role (employee_id, role_id)
VALUES (1, 1);
-- rollback delete from system.ref_employee_role where employee_id=1 and role_id=1;
