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

      <Layout
        css={css`
          display: flex;
          flex-direction: row;
        `}
      >
        <Sider
          breakpoint="xl"
          collapsible
          width={220}
          css={css`
            position: fixed;
            left: 0;
            height: calc(100vh - var(--h-navbar-height));
          `}
        >
          <SideBar />
        </Sider>
        <Content
          css={css`
            margin-left: 220px;
          `}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};
