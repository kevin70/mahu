import { SwitchLang } from '@/components/SwitchLang';
import { SwitchTheme } from '@/components/SwitchTheme';
import { Button, Checkbox, Form, Input, Message, Space } from '@arco-design/web-react';
import { NavLink, useNavigate } from 'react-router';
import { IconUser, IconLock } from '@arco-design/web-react/icon';
import { resolveApiError, TOKEN_API } from '@/services';
import { useProfileStore, useTokenStore } from '@/stores';
import { useMutation } from '@tanstack/react-query';
import { css } from '@emotion/react';

const LoginForm = () => {
  const title = import.meta.env.VITE_APP_TITLE;
  const appName = import.meta.env.VITE_APP_NAME;
  const clientId = import.meta.env.VITE_CLIENT_ID;

  const navigate = useNavigate();
  const tokenStore = useTokenStore();
  const profileStore = useProfileStore();

  const { isPending, isError, error, mutateAsync } = useMutation({
    async mutationFn(values: any) {
      try {
        const rs = await TOKEN_API.login({
          loginRequest: {
            grantType: 'password',
            clientId: clientId,
            ...values,
          },
        });

        // 保存访问令牌
        tokenStore.clean();
        tokenStore.attachToken(rs.accessToken, rs.refreshToken, rs.expiresIn, values.rememberMe);

        // 刷新个人信息
        const profile = await profileStore.refreshProfile();
        Message.info(`欢迎回来 - ${profile.nickname}！`);
        return rs;
      } catch (e) {
        // FIXME 处理密码错误
        throw await resolveApiError(e);
      }
    },
    onSuccess() {
      navigate('/dashboard', { replace: true });
    },
    onError(error, variables, context) {
      console.error('登录失败', variables, error, context);
    },
  });

  return (
    <div
      css={css`
        width: 400px;
      `}
    >
      <div
        css={css`
          text-align: center;
          font-size: 24px;
          font-weight: 500;
          line-height: 3;
          color: var(--color-text-1);
        `}
      >
        {title}
      </div>

      <Form layout="vertical" onSubmit={mutateAsync}>
        <Form.Item field="username">
          <Input placeholder="用户名" prefix={<IconUser />} />
        </Form.Item>
        <Form.Item field="password">
          <Input.Password placeholder="密码" prefix={<IconLock />} />
        </Form.Item>

        <Space direction="vertical">
          <div
            css={css`
              display: flex;
              justify-content: space-between;
            `}
          >
            <Checkbox>记住我</Checkbox>
            <NavLink to={'/forget-password'}>忘记密码</NavLink>
          </div>

          <Button htmlType="submit" type="primary" long loading={isPending}>
            登录
          </Button>
        </Space>
      </Form>
    </div>
  );
};

export const Login = () => {
  return (
    <>
      <div
        css={css`
          position: fixed;
          top: 20px;
          right: 20px;
          z-index: 100;
          display: flex;
          gap: 16px;
          align-items: center;
        `}
      >
        <SwitchTheme />
        <SwitchLang />
      </div>

      <div
        css={css`
          height: 100vh;
          width: 100vw;
          display: flex;
          justify-content: center;
          align-items: center;
        `}
      >
        <LoginForm />
      </div>
    </>
  );
};
