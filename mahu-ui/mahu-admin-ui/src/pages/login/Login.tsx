import { useProfileStore, useTokenStore } from '@/stores';
import { LockOutlined, UserOutlined, WechatOutlined, WeiboOutlined } from '@ant-design/icons';
import { LoginFormPage, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { Alert, Button, Divider, Flex, message, Space } from 'antd';
import { useNavigate } from 'react-router';
import { SwitchLang } from '@/components/SwitchLang';
import { SwitchTheme } from '@/components/SwitchTheme';
import { css } from '@styled-system/css';
import { loginMutation } from '@/client/@tanstack/react-query.gen';
import { TokenPasswordForm } from '@/client';
import { useState } from 'react';

export const Login = () => {
  const title = import.meta.env.VITE_APP_TITLE;
  const appName = import.meta.env.VITE_APP_NAME;
  const clientId = import.meta.env.VITE_CLIENT_ID;

  const navigate = useNavigate();
  const tokenStore = useTokenStore();
  const profileStore = useProfileStore();
  const [rememberMe, setRememberMe] = useState(false);

  const { isPending, isError, error, mutateAsync } = useMutation({
    ...loginMutation(),
    async onSuccess(data, variables) {
      // 保存访问令牌
      tokenStore.clean();
      tokenStore.attachToken(data.access_token, data.refresh_token, data.expires_in, rememberMe);

      // 刷新个人信息
      const profile = await profileStore.refreshProfile();
      message.info(`欢迎回来 - ${profile.nickname}！`);

      // 保存上次登录的用户名
      localStorage.setItem('username', (variables.body as TokenPasswordForm).username);
      navigate('/dashboard', { replace: true });
    },
    onError(error, variables, context) {
      console.error('登录失败', variables, error, context);
    },
  });

  const actions = (
    <Flex vertical justify="center" align="center">
      <Divider plain>
        <span
          className={css`
            color: var(--ant-color-text-placeholder);
          `}
        >
          其他登录方式
        </span>
      </Divider>
      <Space align="center">
        <Button disabled shape="circle" icon={<WechatOutlined />} />
        <Button disabled shape="circle" icon={<WeiboOutlined />} />
      </Space>
    </Flex>
  );

  return (
    <div
      className={css`
        height: 100vh;
        box-sizing: border-box;
      `}
    >
      <div
        className={css`
          z-index: 999;
          position: fixed;
          top: 0;
          right: 0;
          background-color: var(--ant-color-primary-bg);
          display: flex;
          align-items: center;
          padding: 4px;
          gap: 4px;
        `}
      >
        <SwitchTheme />
        <SwitchLang />
      </div>

      <LoginFormPage
        backgroundVideoUrl="https://gw.alipayobjects.com/v/huamei_gcee1x/afts/video/jXRBRK_VAwoAAAAAAAAAAAAAK4eUAQBr"
        title={title}
        subTitle={appName}
        loading={isPending}
        initialValues={{
          username: localStorage.getItem('username'),
        }}
        onFinish={async (values: TokenPasswordForm) => {
          await mutateAsync({
            body: {
              ...values,
              grant_type: 'password',
              client_id: clientId,
            },
          });
        }}
        actions={actions}
        message={isError && <Alert showIcon banner closable type="error" message={error.message} />}
      >
        <ProFormText
          name="username"
          fieldProps={{
            size: 'large',
            prefix: <UserOutlined />,
          }}
          placeholder={'用户名'}
          rules={[
            {
              required: true,
              message: '请输入用户名',
            },
          ]}
        />
        <ProFormText.Password
          name="password"
          fieldProps={{
            size: 'large',
            prefix: <LockOutlined />,
          }}
          placeholder={'密码'}
          rules={[
            {
              required: true,
              message: '请输入密码',
            },
          ]}
        />

        <Flex
          justify="space-between"
          align="center"
          className={css`
            margin-block-end: var(--ant-margin);
          `}
        >
          <ProFormCheckbox
            noStyle
            name="rememberMe"
            fieldProps={{
              onChange(e) {
                setRememberMe(e.target.checked);
              },
            }}
          >
            记住我
          </ProFormCheckbox>
          {/* <Button type="link">忘记密码</Button> */}
        </Flex>
      </LoginFormPage>
    </div>
  );
};
