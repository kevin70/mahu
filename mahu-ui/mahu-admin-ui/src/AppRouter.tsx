import { createBrowserRouter, createRoutesFromElements, Route } from 'react-router';
import { App } from './App';
import { Login } from '@/pages/login/Login';

// prettier-ignore-start
export const routes = createRoutesFromElements(
  <Route path="/" element={<App />}>
    <Route path="/login" element={<Login />} />
  </Route>
);
// prettier-ignore-end

export const appRouter = createBrowserRouter(routes);
