import { useState } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router';
import { useProfileStore, useTokenStore } from '@/stores';
import { message, Spin } from 'antd';
import { useAsyncEffect, useInterval, useTimeout, useUnmount } from 'ahooks';
import { resolveApiError } from './services';
import { css } from '@emotion/css';

function App() {
  const navigate = useNavigate();
  const location = useLocation();

  const [initing, setIniting] = useState(true);
  const tokenStore = useTokenStore();
  const profileStore = useProfileStore();

  const SplashScreen = () => {
    return (
      <div
        className={css`
          height: 100vh;
          width: 100vw;
          display: flex;
          justify-content: center;
          align-items: center;
        `}
      >
        <Spin size="large" tip="加载中...">
          <div
            className={css`
              padding: 50px;
            `}
          ></div>
        </Spin>
      </div>
    );
  };

  // ========================== 全局函数 ========================== //
  window.$accessToken = () => {
    if (!tokenStore.isAccessTokenValid()) {
      message.error('认证失效');
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
  // ========================== 全局函数 ========================== //

  // 初始化超时检查
  const clearInitingTimeout = useTimeout(() => {
    setIniting((prev) => {
      if (prev) {
        console.error('初始化超时');
        message.error('初始化超时');
      }
      return false;
    });
  }, 15 * 1000);

  // 检查并刷新令牌
  const clearTokenInterval = useInterval(() => tokenStore.checkAndRefreshToken(), 5 * 60 * 1000);

  useAsyncEffect(async () => {
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
            message: '连接服务器失败，请刷新重试',
          });
        } else {
          $showAlert({
            message: '初始化用户信息失败',
          });
          $gotoLogin();
        }
      }
    }

    setIniting(false);
  }, []);

  // 清理资源
  useUnmount(() => {
    clearInitingTimeout();
    clearTokenInterval();
  });

  return initing ? <SplashScreen /> : <Outlet />;
}

export default App;
