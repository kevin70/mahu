import { Layout } from '@arco-design/web-react';
import { NavBar } from '@/components/NavBar';
import { Outlet } from 'react-router';
import { SideBar } from './components/SideBar';
import { css } from '@emotion/react';

export const ClassicLayout = () => {
  const { Header, Content, Sider } = Layout;

  return (
    <Layout
      css={css`
        width: 100%;
        height: 100%;
      `}
    >
      <Header
        css={css`
          padding-bottom: var(--h-navbar-height);
        `}
      >
        <NavBar />
      </Header>

      <Layout>
        <Sider
          breakpoint="xl"
          css={css`
            height: calc(100vh - var(--h-navbar-height));
          `}
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
