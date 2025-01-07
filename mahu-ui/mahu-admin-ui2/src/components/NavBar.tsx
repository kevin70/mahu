import { css } from '@styled-system/css';
import { flex } from '@styled-system/patterns';
import Logo from '@/assets/logo.svg';
import { Avatar, Divider, Dropdown, Input, Menu, Space } from '@arco-design/web-react';
import { SwitchLang } from './SwitchLang';
import { SwitchTheme } from './SwitchTheme';
import { IconEdit, IconRobot, IconUser } from '@arco-design/web-react/icon';

export const NavBar = () => {
  const title = import.meta.env.VITE_APP_TITLE;

  const AvatarNickname = () => {
    return (
      <Dropdown
        droplist={
          <Menu>
            <Menu.Item key="profile">
              <Space>
                <IconUser />
                个人信息
              </Space>
            </Menu.Item>
            <Menu.Item key="updateMePassword">
              <Space>
                <IconEdit />
                修改密码
              </Space>
            </Menu.Item>
            <Divider style={{ margin: '4px 0' }} />
            <Menu.Item
              key="logout"
              onClick={() => {
                $gotoLogin();
              }}
            >
              <Space className={css({ color: 'rgb(var(--red-7))' })}>
                <IconRobot />
                退出登录
              </Space>
            </Menu.Item>
          </Menu>
        }
        position="br"
        trigger={'click'}
      >
        <Avatar style={{ cursor: 'pointer' }}>头像</Avatar>
      </Dropdown>
    );
  };

  return (
    <div
      className={flex({
        h: 'var(--h-navbar-height)',
        w: 'full',
        position: 'fixed',
        top: 0,
        left: 0,
        zIndex: 100,
        justify: 'space-between',
        align: 'center',
        border: '1px solid var(--color-border)',
        boxSizing: 'border-box',
        bg: 'var(--color-bg-2)',
      })}
    >
      <div
        className={flex({
          align: 'center',
          pl: 4,
        })}
      >
        <Logo />
        <div
          className={css({
            color: 'var(--color-text-1)',
            fontWeight: 500,
            fontSize: 20,
            ml: 4,
          })}
        >
          {title}
        </div>
      </div>

      <Space
        size={'medium'}
        className={css({
          mr: 4,
        })}
      >
        <Input.Search />
        <SwitchTheme />
        <SwitchLang />
        <AvatarNickname />
      </Space>
    </div>
  );
};
