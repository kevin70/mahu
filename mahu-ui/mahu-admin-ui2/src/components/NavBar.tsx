import Logo from '@/assets/logo.svg';
import { Avatar, Divider, Dropdown, Input, Menu, Space } from '@arco-design/web-react';
import { SwitchLang } from './SwitchLang';
import { SwitchTheme } from './SwitchTheme';
import { IconEdit, IconRobot, IconUser } from '@arco-design/web-react/icon';
import { css } from '@emotion/react';

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
              <Space>
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
      css={css`
        height: var(--h-navbar-height);
        width: 100%;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 100;
        justify-content: space-between;
        align-items: center;
        border: 1px solid var(--color-border);
        background: var(--color-bg-2);
        box-sizing: border-box;
      `}
    >
      <div
        css={css`
          display: flex;
          align-items: center;
          padding-left: 4;
        `}
      >
        <Logo />
        <div
          css={css`
            color: var(--color-text-1);
            font-weight: 500;
            font-size: 20;
            margin-left: 4;
          `}
        >
          {title}
        </div>
      </div>

      <Space
        size={'medium'}
        css={css`
          margin-right: 4;
        `}
      >
        <Input.Search />
        <SwitchTheme />
        <SwitchLang />
        <AvatarNickname />
      </Space>
    </div>
  );
};
