import { css, cx } from '@styled-system/css';
import { flex } from '@styled-system/patterns';
import Logo from '@/assets/logo.svg';
import { Avatar, Divider, Dropdown, Input, Menu } from '@arco-design/web-react';
import '@arco-design/web-react';
import IconParkOutlineLogout from '@/icons/IconParkOutlineLogout';
import { SwitchLang } from './SwitchLang';
import { SwitchTheme } from './SwitchTheme';

export const NavBar = () => {
  const title = import.meta.env.VITE_APP_TITLE;

  const droplist = (
    <Menu>
      <Divider style={{ margin: '4px 0' }} />
      <Menu.Item key="logout">
        <div className={flex({ gap: 2, align: 'center', color: 'rgb(var(--red-7))' })}>
          <IconParkOutlineLogout fontSize={18} />
          退出登录
        </div>
      </Menu.Item>
    </Menu>
  );

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

      <div
        className={flex({
          pr: 4,
          gap: 4,
          align: 'center',
        })}
      >
        <Input.Search />
        <SwitchTheme />
        <SwitchLang />
        <Dropdown droplist={droplist} position="br" trigger={'click'}>
          <Avatar size={40} style={{ cursor: 'pointer' }}>
            <img alt="avatar" />
          </Avatar>
        </Dropdown>
      </div>
    </div>
  );
};
