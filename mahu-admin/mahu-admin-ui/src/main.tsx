import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ConfigProvider, theme } from 'antd';
import 'antd/dist/reset.css';
import zhCN from 'antd/locale/zh_CN';

import 'dayjs';
import 'dayjs/locale/zh-cn';

import { appRouter } from './AppRouter.tsx';
import { useAppStore } from '@/stores';
import { useShallow } from 'zustand/shallow';

export const Root = () => {
  const isLightTheme = useAppStore(useShallow((state) => state.isLightTheme()));

  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        algorithm: isLightTheme ? theme.defaultAlgorithm : theme.darkAlgorithm,
        cssVar: true,
        token: {
          borderRadius: 0,
        },
      }}
    >
      <QueryClientProvider client={new QueryClient({})}>
        <RouterProvider router={appRouter} />
      </QueryClientProvider>
    </ConfigProvider>
  );
};

createRoot(document.getElementById('root')!).render(<Root />);
