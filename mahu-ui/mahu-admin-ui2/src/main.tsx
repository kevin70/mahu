import { StrictMode, useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Route, Routes } from 'react-router';

import './index.css';
import '@arco-design/web-react/dist/css/arco.css';
import { ConfigProvider, Spin } from '@arco-design/web-react';
import zhCN from '@arco-design/web-react/es/locale/zh-CN';
import enUS from '@arco-design/web-react/es/locale/en-US';

import { Flex } from '@styled-system/jsx';
import { css } from '@styled-system/css';

import { App } from './App';
import { Login } from '@/pages/login/Login';

import { useAppStore } from '@/stores';
import { useShallow } from 'zustand/shallow';

import i18n from '@/locales/index.ts';
import { I18nextProvider } from 'react-i18next';
import { Dashboard } from './pages/dashboard/Dashboard';

const SplashScreen = () => {
  return (
    <Flex justify="center" align="center" className={css({ h: '100vh', w: '100vw' })}>
      <Spin size={40} tip="加载中..." />
    </Flex>
  );
};

export const Root = () => {
  const [isLoading, setLoading] = useState(true);

  setTimeout(() => {
    setLoading(false);
  }, 200);

  // 主题
  const theme = useAppStore(useShallow((state) => state.theme));
  useEffect(() => {
    if (theme === 'dark') {
      document.body.setAttribute('arco-theme', 'dark');
    } else {
      document.body.removeAttribute('arco-theme');
    }
  }, [theme]);

  // 语言
  const [locale, setLocale] = useState(zhCN);
  i18n.on('languageChanged', (lng) => {
    if (lng === 'en-US') {
      setLocale(enUS);
    } else {
      setLocale(zhCN);
    }
  });

  return (
    <BrowserRouter>
      <I18nextProvider i18n={i18n}>
        <ConfigProvider locale={locale}>
          {isLoading ? (
            <SplashScreen />
          ) : (
            <Routes>
              <Route path="/" Component={App}>
                <Route path="/dashboard" Component={Dashboard} />
              </Route>
              <Route path="/login" Component={Login} />
            </Routes>
          )}
        </ConfigProvider>
      </I18nextProvider>
    </BrowserRouter>
  );
};

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Root />
  </StrictMode>
);
