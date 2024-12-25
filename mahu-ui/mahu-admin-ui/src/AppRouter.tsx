import { createBrowserRouter, createRoutesFromElements, Route } from 'react-router';
import { App } from './App';

// prettier-ignore-start
export const routes = createRoutesFromElements(
  <Route path="/" element={<App />}>
    <Route path="/login" element={<div>Login</div>} />
  </Route>
);
// prettier-ignore-end

export const appRouter = createBrowserRouter(routes);
