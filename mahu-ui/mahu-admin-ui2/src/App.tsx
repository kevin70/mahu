import { Route, Routes } from 'react-router';
import { ClassicLayout } from './ClassicLayout';
import { Dashboard } from './pages/dashboard/Dashboard';
import { EmployeeList } from './pages/system/employee/EmployeeList';
import { Login } from './pages/login/Login';

export const App = () => {
  return (
    <Routes>
      <Route path="/" Component={ClassicLayout}>
        <Route path="/dashboard" Component={Dashboard} />
        <Route path="/system/employee-list" Component={EmployeeList} />
      </Route>
      <Route path="/login" Component={Login} />
    </Routes>
  );
};
