import { useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import 'antd/dist/reset.css';
import { Alert, ConfigProvider, theme } from 'antd';
import './index.css';

import zhCN from 'antd/locale/zh_CN';
import enUS from 'antd/locale/en_US';

import i18n from '@/locales/index.ts';
import { I18nextProvider } from 'react-i18next';

import { appRouter } from './AppRouter.tsx';
import { useAppStore, useTokenStore } from '@/stores';
import { useShallow } from 'zustand/shallow';

import { IconContext } from 'react-icons';
import { useSet } from 'ahooks';
import { AlertProps } from 'antd/lib/index';
import { ulid } from 'ulid';
import { css } from '@styled-system/css';

import { client as apiClient } from '@/client/client.gen.ts';
import axios, { AxiosError } from 'axios';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // 默认缓存5分钟
      staleTime: 5 * 60 * 1000,
    },
  },
});

// 初始化 API 客户端
const setupApiClient = (getToken: () => Promise<string>) => {
  apiClient.setConfig({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    auth({ scheme }) {
      if (scheme === 'bearer') {
        return getToken();
      }
      throw new Error(`不支持的 auth scheme: ${scheme}`);
    },
  });

  apiClient.instance.interceptors.response.use(
    (response) => response,
    (error) => {
      if (axios.isAxiosError(error) && error.response?.status === 401) {
        // if (!window.location.pathname.startsWith('/login')) {
        //   window.location.href = '/login';
        // }
        // return Promise.reject(new Error('Session expired'));
      }

      console.error('接口响应错误', error);
      const errResp = error.response?.data?.error;
      if (errResp) {
        throw new AxiosError(errResp.message, `${errResp.code}`, error.config, error.request, error.response);
      }
      throw error;
    }
  );
};

export const Root = () => {
  const isLightTheme = useAppStore(useShallow((state) => state.isLightTheme()));
  const [locale, setLocale] = useState(zhCN);
  const tokenStore = useTokenStore();
  setupApiClient(() => tokenStore.obtainAccessToken());

  // ===================== 全局警告消息提示 ===================== //
  const [alertSet, { add: addAlert, remove: removeAlert }] = useSet<
    { key: string } & Pick<AlertProps, 'type' | 'message'>
  >([]);
  const alertMessages = useMemo(() => Array.from(alertSet.values()), [alertSet]);
  const GlobalAlert = () => (
    <div
      className={css`
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        display: flex;
        flex-direction: column;
        row-gap: var(--ant-margin);
        z-index: 999999;
      `}
    >
      {alertMessages.map((v) => (
        <Alert
          key={v.key}
          showIcon
          banner
          closable
          type={v.type || 'error'}
          message={v.message}
          onClose={() => removeAlert(v)}
        />
      ))}
    </div>
  );
  // ===================== 全局警告消息提示 ===================== //

  // 全局警告消息
  window.$showAlert = (args: Pick<AlertProps, 'type' | 'message'>) => {
    addAlert({
      key: ulid(),
      ...args,
    });
  };

  i18n.on('languageChanged', (lng) => {
    if (lng === 'enUS') {
      setLocale(enUS);
      return;
    }
    setLocale(zhCN);
  });

  const iconContextValue = useMemo(
    () => ({
      style: {
        fontSize: 16,
      },
    }),
    []
  );
  return (
    <IconContext.Provider value={iconContextValue}>
      <I18nextProvider i18n={i18n}>
        <ConfigProvider
          locale={locale}
          theme={{
            algorithm: isLightTheme ? theme.defaultAlgorithm : theme.darkAlgorithm,
            cssVar: true,
            hashed: false,
            token: {
              borderRadius: 0,
            },
          }}
        >
          {/** 全局警告消息 */}
          <GlobalAlert />

          <QueryClientProvider client={queryClient}>
            <RouterProvider router={appRouter} />
          </QueryClientProvider>
        </ConfigProvider>
      </I18nextProvider>
    </IconContext.Provider>
  );
};

createRoot(document.getElementById('root')!).render(<Root />);
