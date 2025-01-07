import { SwitchLang } from '@/components/SwitchLang';
import { SwitchTheme } from '@/components/SwitchTheme';
import { Button, Checkbox, Form, Input, Message, Space } from '@arco-design/web-react';
import { css } from '@styled-system/css';
import { Flex } from '@styled-system/jsx';
import { flex } from '@styled-system/patterns';
import { NavLink, useNavigate } from 'react-router';
import { IconUser, IconLock } from '@arco-design/web-react/icon';
import { resolveApiError, TOKEN_API } from '@/services';
import { useProfileStore, useTokenStore } from '@/stores';
import { useMutation } from '@tanstack/react-query';

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
      className={css({
        w: '400px',
      })}
    >
      <div
        className={css({
          display: 'inline-flex',
          fontSize: 24,
          fontWeight: 500,
          color: 'var(--color-text-1)',
          lineHeight: 3,
        })}
      >
        登录 Arco Design Pro
      </div>

      <Form layout="vertical" onSubmit={mutateAsync}>
        <Form.Item field="username">
          <Input placeholder="用户名" prefix={<IconUser />} />
        </Form.Item>
        <Form.Item field="password">
          <Input.Password placeholder="密码" prefix={<IconLock />} />
        </Form.Item>

        <Space direction="vertical">
          <div className={css({ display: 'flex', justifyContent: 'space-between' })}>
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
    <Flex h={'100vh'} justify={'center'} align={'center'}>
      <div
        className={flex({
          pos: 'fixed',
          top: 4,
          right: 4,
          zIndex: 100,
          gap: 4,
          align: 'center',
        })}
      >
        <SwitchLang />
        <SwitchTheme />
      </div>

      <LoginForm />
    </Flex>
  );
};
