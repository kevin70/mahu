import { Layout } from '@arco-design/web-react';
import { css } from '@styled-system/css';
import { NavBar } from '@/components/NavBar';
import { Outlet } from 'react-router';
import { SideBar } from './components/SideBar';

export const ClassicLayout = () => {
  const { Header, Content, Sider } = Layout;

  return (
    <Layout className={css({ w: 'full', h: 'full' })}>
      <Header
        className={css({
          paddingBottom: 'var(--h-navbar-height)',
        })}
      >
        <NavBar />
      </Header>

      <Layout>
        <Sider
          breakpoint="xl"
          className={css({
            h: 'calc(100vh - var(--h-navbar-height))',
          })}
        >
          <SideBar />
        </Sider>
        <Content>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};
