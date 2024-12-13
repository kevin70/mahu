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
import { AccessLogList } from '@/pages/system/access-log/AccessLogList';
import { RoleList } from '@/pages/system/role/RoleList';
import { AuditJourList } from '@/pages/system/audit-jour/AuditJourList';

import { BrandList } from './pages/brand/BrandList';

import { MarketShopList } from '@/pages/market/shop/MarketShopList';

// prettier-ignore-start
export const routes = createRoutesFromElements(
  <Route path="/" element={<App />}>
    <Route path="/login" element={<Login />} />

    <Route element={<AndDesignProLayout />}>
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/me/:kind" element={<MeProfile />} />

      {/** 基础模块 */}
      <Route path="/brand-list" element={<BrandList />} />

      {/** 商城模块 */}
      <Route path="/market/shop-list" element={<MarketShopList />} />

      {/** 系统模块 */}
      <Route path="/system/employee-list" element={<EmployeeList />} />
      <Route path="/system/department-list" element={<DepartmentList />} />
      <Route path="/system/role-list" element={<RoleList />} />
      <Route path="/system/dict-list" element={<DictList />} />
      <Route path="/system/client-list" element={<ClientList />} />
      <Route path="/system/access-log-list" element={<AccessLogList />} />
      <Route path="/system/audit-jour-list" element={<AuditJourList />} />
    </Route>
  </Route>
);
// prettier-ignore-end

export const appRouter = createBrowserRouter(routes);
