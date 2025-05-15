--liquibase formatted sql

-- 数据库 Schema
--include file:changelog-schemas/schemas.sql

-- 系统表
--include file:changelog-tables/system.dict.sql
--include file:changelog-tables/system.client.sql
--include file:changelog-tables/system.role.sql
--include file:changelog-tables/system.admin.sql
--include file:changelog-tables/system.admin_role.sql
--include file:changelog-tables/system.department.sql

--include file:changelog-tables/public.scheduled_tasks.sql
--include file:changelog-tables/public.users.sql
--include file:changelog-tables/public.region.sql

-- 业务日志表
--include file:changelog-tables/log.admin_access_log.sql
--include file:changelog-tables/log.admin_audit_log.sql
--include file:changelog-tables/log.admin_auth_log.sql
--include file:changelog-tables/log.scheduled_execution_log.sql

-- 初始化业务数据
--include file:changelog-tables/0_init_data.sql
