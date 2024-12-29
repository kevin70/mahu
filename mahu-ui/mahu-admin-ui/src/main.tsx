import { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ConfigProvider, theme } from 'antd';

import './index.css';

import zhCN from 'antd/locale/zh_CN';
import enUS from 'antd/locale/en_US';

import 'dayjs';
import 'dayjs/locale/zh-cn';

import i18n from '@/locales/index.ts';
import { I18nextProvider } from 'react-i18next';

import { appRouter } from './AppRouter.tsx';
import { useAppStore } from '@/stores';
import { useShallow } from 'zustand/shallow';

export const Root = () => {
  const isLightTheme = useAppStore(useShallow((state) => state.isLightTheme()));
  const [locale, setLocale] = useState(zhCN);

  useEffect(() => {
    console.log('发现的 language: ', i18n.language);
  }, []);

  i18n.on('languageChanged', (lng) => {
    if (lng === 'enUS') {
      setLocale(enUS);
      return;
    }
    setLocale(zhCN);
  });

  return (
    <I18nextProvider i18n={i18n}>
      <ConfigProvider
        locale={locale}
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
    </I18nextProvider>
  );
};

createRoot(document.getElementById('root')!).render(<Root />);
