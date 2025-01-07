import { StrictMode, useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, useNavigate } from 'react-router';

import '@arco-design/web-react/dist/css/arco.css';
import { AlertProps, ConfigProvider, Message, Spin } from '@arco-design/web-react';
import zhCN from '@arco-design/web-react/es/locale/zh-CN';
import enUS from '@arco-design/web-react/es/locale/en-US';

import { Flex } from '@styled-system/jsx';
import { css } from '@styled-system/css';

import { App } from './App';

import { useAppStore, useProfileStore, useTokenStore } from '@/stores';
import { useShallow } from 'zustand/shallow';

import i18n from '@/locales/index.ts';
import { I18nextProvider } from 'react-i18next';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { resolveApiError } from './services';

import './index.css';

const SplashScreen = () => {
  return (
    <Flex justify="center" align="center" className={css({ h: '100vh', w: '100vw' })}>
      <Spin size={40} tip="加载中..." />
    </Flex>
  );
};

export const Root = () => {
  const [initing, setIniting] = useState(true);
  const navigate = useNavigate();
  const profileStore = useProfileStore();
  const tokenStore = useTokenStore();

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

  // ========================== 全局函数 ========================== //
  window.$accessToken = () => {
    if (!tokenStore.isAccessTokenValid()) {
      Message.error('认证失效');
      window.$gotoLogin();
      return Promise.reject('非法的访问令牌');
    }
    return tokenStore.obtainAccessToken();
  };

  window.$checkPermit = (args: string | string[]) => {
    const allPermits: string[] = profileStore.permits;
    // 超级管理员用户拥有所有权限
    if (allPermits.indexOf('*') >= 0) {
      return true;
    }

    const permits = args instanceof Array ? args : [args];
    for (const p of permits) {
      if (allPermits.indexOf(p) >= 0) {
        return true;
      }
    }
    return false;
  };

  window.$checkNotPermit = (args: string | string[]) => !window.$checkPermit(args);

  window.$gotoLogin = () => {
    tokenStore.clean();
    navigate('/login', { replace: true });
  };

  // 全局警告消息
  window.$showAlert = (alert: AlertProps) => {
    // FIXME
  };
  // ========================== 全局函数 ========================== //

  // 检查并刷新令牌
  const clearTokenInterval = setInterval(() => tokenStore.checkAndRefreshToken(), 5 * 60 * 1000);

  useEffect(() => {
    // 初始化超时检查
    const clearInitingTimeout = setTimeout(() => {
      setIniting((prev) => {
        if (prev) {
          console.error('初始化超时');
          Message.error('初始化超时');
        }
        return false;
      });
    }, 15 * 1000);

    (async function () {
      // 初始化操作
      // 1. 校验访问令牌的有效性
      if (!tokenStore.isAccessTokenValid()) {
        $gotoLogin();
        setIniting(false);
        return;
      }

      // 2. 加载个人信息
      if (!profileStore.uid) {
        try {
          await profileStore.refreshProfile();
          if (location.pathname === '/') {
            navigate('/dashboard');
          }
        } catch (e: unknown) {
          const err = await resolveApiError(e);
          if (err.name === 'FETCH_ERROR') {
            $showAlert({
              content: '连接服务器失败，请刷新重试',
            });
          } else {
            $showAlert({
              content: '初始化用户信息失败',
            });
            $gotoLogin();
          }
        }
      }

      setIniting(false);
      clearTimeout(clearInitingTimeout);

      return () => {
        clearInterval(clearTokenInterval);
      };
    })();
  }, []);

  return (
    <I18nextProvider i18n={i18n}>
      <QueryClientProvider client={new QueryClient()}>
        <ConfigProvider locale={locale} theme={{}}>
          {initing ? <SplashScreen /> : <App />}
        </ConfigProvider>
      </QueryClientProvider>
    </I18nextProvider>
  );
};

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <Root />
    </BrowserRouter>
  </StrictMode>
);
