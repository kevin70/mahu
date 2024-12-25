import { Layout, Spin } from '@douyinfe/semi-ui';
import { Flex } from '@styled-system/jsx';
import { css } from '@styled-system/css';
import { ClassicLayout } from './layout/ClassicLayout';
import { Outlet } from 'react-router';

export const App = () => {
  const { Header, Footer, Sider, Content } = Layout;

  const SplashScreen = () => {
    return (
      <Flex
        justify="center"
        align="center"
        className={css({
          h: '100vh',
          w: '100vw',
        })}
      >
        <Spin size="large" tip="加载中...">
          <div className={css({ mr: 200 })}></div>
        </Spin>
      </Flex>
    );
  };

  // return <SplashScreen />;
  return <Outlet />;
};
