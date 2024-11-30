import { createRoutesFromElements, Route } from 'react-router';
import { createBrowserRouter } from 'react-router-dom';

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

// prettier-ignore-start
export const routes = createRoutesFromElements(
  <Route path="/" element={<App />}>
    <Route path="/login" element={<Login />} />

    <Route element={<AndDesignProLayout />}>
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/me/:kind" element={<MeProfile />} />

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
