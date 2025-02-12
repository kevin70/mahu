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
import { useAppStore } from '@/stores';
import { useShallow } from 'zustand/shallow';

import { IconContext } from 'react-icons';
import { useSet } from 'ahooks';
import { AlertProps } from 'antd/lib/index';
import { ulid } from 'ulid';
import { css } from '@styled-system/css';

export const Root = () => {
  const isLightTheme = useAppStore(useShallow((state) => state.isLightTheme()));
  const [locale, setLocale] = useState(zhCN);

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

  return (
    <IconContext.Provider value={{ style: { fontSize: 16 } }}>
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

          <QueryClientProvider client={new QueryClient({})}>
            <RouterProvider router={appRouter} />
          </QueryClientProvider>
        </ConfigProvider>
      </I18nextProvider>
    </IconContext.Provider>
  );
};

createRoot(document.getElementById('root')!).render(<Root />);
