import { resolveApiError, TOKEN_API } from '@/services';
import { useProfileStore, useTokenStore } from '@/stores';
import { LockOutlined, UserOutlined, WechatOutlined, WeiboOutlined } from '@ant-design/icons';
import { LoginFormPage, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { Alert, Button, Divider, Flex, Space } from 'antd';
import { useNavigate } from 'react-router';
import { css } from '@styled-system/css';

export const Login = () => {
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
          grantType: 'password',
          clientId: clientId,
          ...values,
        });

        // 保存访问令牌
        tokenStore.attachToken(rs.accessToken, rs.refreshToken, rs.expiresIn, values.rememberMe);

        // 刷新个人信息
        const profile = await profileStore.refreshProfile();
        $message().info(`欢迎回来 - ${profile.nickname}！`);
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

  const actions = (
    <Flex vertical justify="center" align="center">
      <Divider plain>
        <span
          className={css`
            color: var(--ant-color-text-placeholder);
            font-weight: normal;
            font-size: 14;
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
      `}
    >
      <LoginFormPage
        backgroundVideoUrl="https://gw.alipayobjects.com/v/huamei_gcee1x/afts/video/jXRBRK_VAwoAAAAAAAAAAAAAK4eUAQBr"
        title={title}
        subTitle={appName}
        loading={isPending}
        onFinish={async (values) => {
          await mutateAsync(values);
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
          <ProFormCheckbox noStyle name="rememberMe">
            记住我
          </ProFormCheckbox>
          {/* <Button type="link">忘记密码</Button> */}
        </Flex>
      </LoginFormPage>
    </div>
  );
};
