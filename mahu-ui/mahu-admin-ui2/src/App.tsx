import { Route, Routes } from 'react-router';
import { ClassicLayout } from './ClassicLayout';
import { Dashboard } from './pages/dashboard/Dashboard';
import { EmployeeList } from './pages/system/employee/EmployeeList';
import { Login } from './pages/login/Login';
import { MeProfile } from './pages/me/MeProfile';
import { AccessLogList } from './pages/system/access-log/AccessLogList';
import { AuditJourList } from './pages/system/audit-jour/AuditJourList';

export const App = () => {
  return (
    <Routes>
      <Route path="/" Component={ClassicLayout}>
        <Route path="/me" Component={MeProfile} />

        <Route path="/dashboard" Component={Dashboard} />
        <Route path="/system/employee-list" Component={EmployeeList} />
        <Route path="/system/access-log-list" Component={AccessLogList} />
        <Route path="/system/audit-jour-list" Component={AuditJourList} />
      </Route>
      <Route path="/login" Component={Login} />
    </Routes>
  );
};
