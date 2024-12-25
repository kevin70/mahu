import { Outlet } from 'react-router';
import { Layout } from '@douyinfe/semi-ui';
import { css } from '@styled-system/css';
import { SiderNav } from './SiderNav';
import { UserNav } from './UserNav';

export const ClassicLayout = () => {
  const { Header, Sider, Content } = Layout;
  return (
    <Layout>
      <Header
        className={css({ w: '100%', h: '60px', bg: 'var(--semi-color-fill-0)', pos: 'fixed', top: 0, zIndex: 999 })}
      >
        <UserNav />
      </Header>
      <Layout>
        <Sider>
          <SiderNav
            className={css({
              w: '240px',
              h: 'calc(100% - 60px)',
              pos: 'fixed',
              top: '64px',
              left: 0,
            })}
          />
        </Sider>

        <Content
          className={css({
            h: 'calc(100% - 60px)',
            pos: 'fixed',
            top: '64px',
            left: '240px',
          })}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};
