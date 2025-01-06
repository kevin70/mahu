import { Layout, Menu } from '@arco-design/web-react';
import { css } from '@styled-system/css';
import { NavBar } from '@/components/NavBar';
import { Outlet } from 'react-router';

export const ClassicLayout = () => {
  const { Header, Content, Sider } = Layout;

  const MenuItem = Menu.Item;
  const SubMenu = Menu.SubMenu;

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
        <Sider breakpoint="xl" collapsible width={248}>
          <Menu defaultOpenKeys={['1']} defaultSelectedKeys={['0_3']} style={{ width: '100%' }}>
            <MenuItem key="0_1" disabled>
              Menu 1
            </MenuItem>
            <MenuItem key="0_2">Menu 2</MenuItem>
            <MenuItem key="0_3">Menu 3</MenuItem>
            <SubMenu key="1" title={<span>Navigation 1</span>}>
              <MenuItem key="1_1">Menu 1</MenuItem>
              <MenuItem key="1_2">Menu 2</MenuItem>
              <SubMenu key="2" title="Navigation 2">
                <MenuItem key="2_1">Menu 1</MenuItem>
                <MenuItem key="2_2">Menu 2</MenuItem>
              </SubMenu>
              <SubMenu key="3" title="Navigation 3">
                <MenuItem key="3_1">Menu 1</MenuItem>
                <MenuItem key="3_2">Menu 2</MenuItem>
                <MenuItem key="3_3">Menu 3</MenuItem>
              </SubMenu>
            </SubMenu>
            <SubMenu key="4" title={<span>Navigation 4</span>}>
              <MenuItem key="4_1">Menu 1</MenuItem>
              <MenuItem key="4_2">Menu 2</MenuItem>
              <MenuItem key="4_3">Menu 3</MenuItem>
            </SubMenu>
          </Menu>
        </Sider>
        <Content
          className={css({
            p: 4,
          })}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};
