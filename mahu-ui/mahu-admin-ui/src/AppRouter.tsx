import { createBrowserRouter, createRoutesFromElements, Route } from 'react-router';

import App from '@/App';
import { AndDesignProLayout } from '@/layouts/AntDesignProLayout';
import { Login } from '@/pages/login/Login';
import { Dashboard } from '@/pages/dashboard/Dashboard';
import { MeProfile } from '@/pages/me/MeProfile';

import { EmployeeList } from '@/pages/system/employee/EmployeeList';
import { DepartmentList } from '@/pages/system/department/DepartmentList';
import { DictList } from '@/pages/system/dict/DictList';
import { ClientList } from '@/pages/system/client/ClientList';
import { RoleList } from '@/pages/system/role/RoleList';
import { AccessLogList } from '@/pages/system/admin-access-log/AccessLogList';
import { AuditLogList } from '@/pages/system/admin-audit-log/AuditLogList';
import { AuthLogList } from '@/pages/system/admin-auth-log/AuthLogList';
import { ScheduledTaskList } from '@/pages/system/scheduled-task/ScheduledTaskList';

import { BrandList } from '@/pages/brand/BrandList';

import { MartCategoryList } from '@/pages/mart/category/MartCategoryList';
import { MartShopList } from '@/pages/mart/shop/MartShopList';
import { MartAssetList } from '@/pages/mart/asset/MartAssetList';
import { MartAttributeList } from '@/pages/mart/attribute/MartAttributeList';
import { MartProductList } from '@/pages/mart/product/MartProductList';
import { MartProductNew } from '@/pages/mart/product/MartProductNew';
import { MartProductEdit } from '@/pages/mart/product/MartProductEdit';

// prettier-ignore-start
export const routes = createRoutesFromElements(
  <Route path="/" element={<App />}>
    <Route path="/login" Component={Login} />

    <Route element={<AndDesignProLayout />}>
      <Route path="/dashboard" Component={Dashboard} />
      <Route path="/me/:kind" Component={MeProfile} />

      {/** 基础模块 */}
      <Route path="/brand-list" Component={BrandList} />

      {/** 商城模块 */}
      <Route path="/mart/category-list" Component={MartCategoryList} />
      <Route path="/mart/attribute-list" Component={MartAttributeList} />
      <Route path="/mart/shop-list" Component={MartShopList} />
      <Route path="/mart/asset-list" Component={MartAssetList} />
      <Route path="/mart/product-list" Component={MartProductList} />
      <Route path="/mart/product-new" Component={MartProductNew} />
      <Route path="/mart/product-edit" Component={MartProductEdit} />

      {/** 系统模块 */}
      <Route path="/system/employee-list" Component={EmployeeList} />
      <Route path="/system/department-list" Component={DepartmentList} />
      <Route path="/system/role-list" Component={RoleList} />
      <Route path="/system/dict-list" Component={DictList} />
      <Route path="/system/client-list" Component={ClientList} />
      <Route path="/system/scheduled-tasks" Component={ScheduledTaskList} />
      <Route path="/logs/admin-access-logs" Component={AccessLogList} />
      <Route path="/logs/admin-audit-logs" Component={AuditLogList} />
      <Route path="/logs/admin-auth-logs" Component={AuthLogList} />
    </Route>
  </Route>
);
// prettier-ignore-end

export const appRouter = createBrowserRouter(routes);
