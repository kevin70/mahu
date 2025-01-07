import { SwitchLang } from '@/components/SwitchLang';
import { SwitchTheme } from '@/components/SwitchTheme';
import { Button, Checkbox, Form, Input, Space } from '@arco-design/web-react';
import { css } from '@styled-system/css';
import { Flex } from '@styled-system/jsx';
import { flex } from '@styled-system/patterns';
import { NavLink } from 'react-router';
import { IconUser, IconLock } from '@arco-design/web-react/icon';

const LoginForm = () => {
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

      <Form layout="vertical">
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

          <Button htmlType="submit" type="primary" long>
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
