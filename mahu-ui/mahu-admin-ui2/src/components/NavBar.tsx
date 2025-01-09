import Logo from '@/assets/logo.svg';
import { Avatar, Divider, Dropdown, Input, Menu, Space } from '@arco-design/web-react';
import { SwitchLang } from './SwitchLang';
import { SwitchTheme } from './SwitchTheme';
import { IconEdit, IconRobot, IconUser } from '@arco-design/web-react/icon';
import { css } from '@emotion/react';
import { Link } from 'react-router';

export const NavBar = () => {
  const title = import.meta.env.VITE_APP_TITLE;

  const AvatarNickname = () => {
    return (
      <Dropdown
        droplist={
          <Menu>
            <Menu.Item key="profile">
              <Link to={'/me?kind=info'}>
                <Space>
                  <IconUser />
                  个人信息
                </Space>
              </Link>
            </Menu.Item>
            <Menu.Item key="updateMePassword">
              <Link to={'/me?kind=updatePassword'}>
                <Space>
                  <IconEdit />
                  修改密码
                </Space>
              </Link>
            </Menu.Item>
            <Divider style={{ margin: '4px 0' }} />
            <Menu.Item
              key="logout"
              onClick={() => {
                $gotoLogin();
              }}
            >
              <Space
                css={css`
                  color: rgb(var(--red-6));
                  font-weight: 500;
                `}
              >
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
        box-sizing: border-box;
        position: fixed;
        top: 0px;
        left: 0px;
        z-index: 100;
        border: 1px solid var(--color-border);
        background: var(--color-bg-2);
        box-sizing: border-box;
        display: flex;
        justify-content: space-between;
        align-items: center;
      `}
    >
      <div
        css={css`
          display: flex;
          align-items: center;
          padding-left: 4px;
        `}
      >
        <Logo />
        <div
          css={css`
            color: var(--color-text-1);
            font-weight: 500;
            font-size: 20px;
            margin-left: 4px;
          `}
        >
          {title}
        </div>
      </div>

      <Space
        size={'medium'}
        css={css`
          margin-right: 4px;
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
