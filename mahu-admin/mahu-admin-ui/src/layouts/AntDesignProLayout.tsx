import { Outlet, useLocation, useNavigate } from 'react-router';
import { ProLayout } from '@ant-design/pro-components';
import { useAppStore, useProfileStore } from '@/stores';
import { useShallow } from 'zustand/shallow';
import { filterMenus, MENUS } from '@/config/menu';
import { Button, Dropdown, Typography } from 'antd';
import { KeyOutlined, LogoutOutlined, MoonOutlined, ProfileOutlined, SunOutlined } from '@ant-design/icons';
import { NavLink } from 'react-router-dom';
import { useMemo } from 'react';
import { ItemType } from 'antd/es/menu/interface';
import { css } from '@styled-system/css';

export const AndDesignProLayout = () => {
  const appStore = useAppStore();
  const isLightTheme = useAppStore(useShallow((state) => state.isLightTheme()));
  const location = useLocation();
  const navigate = useNavigate();

  const { nickname, avatar, permits } = useProfileStore(
    useShallow((state) => ({ nickname: state.nickname, avatar: state.avatar, permits: state.permits }))
  );

  // 系统菜单
  const sysMenus = useMemo(() => filterMenus(MENUS, permits), [permits]);

  // 用户菜单
  const userMenus: ItemType[] = [
    {
      key: 'profile',
      icon: <ProfileOutlined />,
      label: '个人信息',
      onClick() {
        navigate('/me/profile');
      },
    },
    {
      key: 'changePassword',
      icon: <KeyOutlined />,
      label: '修改密码',
      onClick() {
        navigate('/me/change-password');
      },
    },
    {
      type: 'divider',
    },
    {
      icon: (
        <LogoutOutlined
          className={css`
            color: var(--ant-color-error);
          `}
        />
      ),
      label: <Typography.Text type="danger">退出登录</Typography.Text>,
      key: 'logout',
      onClick() {
        $gotoLogin();
      },
    },
  ];

  return (
    <ProLayout
      layout="mix"
      token={{
        pageContainer: {
          paddingBlockPageContainerContent: 16,
          paddingInlinePageContainerContent: 24,
        },
      }}
      fixedHeader
      fixSiderbar
      title={import.meta.env.VITE_APP_TITLE}
      contentStyle={{ paddingBlock: 0, paddingInline: 0 }}
      menu={{
        type: 'group',
      }}
      avatarProps={{
        src: avatar,
        title: nickname,
        render: (_props, dom) => {
          return (
            <Dropdown
              trigger={['click']}
              menu={{
                items: userMenus,
              }}
            >
              {dom}
            </Dropdown>
          );
        },
      }}
      route={{
        path: '/',
        routes: sysMenus,
      }}
      location={location}
      menuItemRender={(item, dom) => {
        if (item.path) {
          return <NavLink to={item.path}>{dom}</NavLink>;
        }
        return <>{dom}</>;
      }}
      actionsRender={(props) => {
        if (props.isMobile) return [];
        if (typeof window === 'undefined') return [];

        return [
          <Button
            type="text"
            shape="circle"
            icon={isLightTheme ? <SunOutlined /> : <MoonOutlined />}
            onClick={() => {
              appStore.changeTheme(isLightTheme ? 'dark' : 'light');
            }}
          />,
        ];
      }}
    >
      <Outlet />
    </ProLayout>
  );
};
