-- liquibase formatted sql


-- changeset kzou227@qq.com:202403211334
create table system.employees_roles
(
    employee_id integer not null,
    role_id     integer not null
);

comment
on column system.employees_roles.employee_id is '用户ID';

comment
on column system.employees_roles.role_id is '角色ID';

create unique index employees_roles_employee_id_role_id_uidx
    on system.employees_roles (employee_id, role_id);
-- rollback drop table system.employees_roles;


-- changeset kzou227@qq.com:202403211357
INSERT INTO system.employees_roles (employee_id, role_id)
VALUES (1, 1);
-- rollback delete from system.employees_roles where employee_id=1 and role_id=1;
