import { createRoot } from 'react-dom/client';
import 'reset-css/reset.css';
import './index.css';
import '@douyinfe/semi-ui/dist/css/semi.css';

import { useShallow } from 'zustand/shallow';
import { ConfigProvider } from '@douyinfe/semi-ui';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider } from 'react-router';

import { useAppStore } from '@/stores/useAppStore.ts';
import { appRouter } from './AppRouter.tsx';

export const Root = () => {
  const isLightTheme = useAppStore(useShallow((state) => state.isLightTheme()));

  return (
    <ConfigProvider>
      <QueryClientProvider client={new QueryClient({})}>
        <RouterProvider router={appRouter} />
      </QueryClientProvider>
    </ConfigProvider>
  );
};

createRoot(document.getElementById('root')!).render(<Root />);
