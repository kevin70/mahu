import { HSaveButton } from '@/components/HSaveButton';
import { PageContainer } from '@/components/PageContainer';
import { Avatar, Form, Input, Tabs } from '@arco-design/web-react';
import { css } from '@emotion/react';

export const MeProfile = () => {
  const InfoPanel = () => {
    return (
      <div
        css={css`
          width: 200px;
          display: flex;
          align-items: center;
          gap: 8px;
        `}
      >
        <Avatar size={64}>头像</Avatar>
        <div>昵称</div>
      </div>
    );
  };

  const UpdatePasswordPanel = () => {
    return (
      <Form layout="vertical">
        <Form.Item field={'originalPassword'} label="原密码" rules={[{ required: true }]}>
          <Input.Password />
        </Form.Item>
        <Form.Item field={'password'} label="新密码" rules={[{ required: true }]}>
          <Input.Password />
        </Form.Item>
        <Form.Item field={'confirmPassword'} label="确认密码" rules={[{ required: true }]}>
          <Input.Password />
        </Form.Item>
        <HSaveButton long />
      </Form>
    );
  };

  return (
    <PageContainer title={'个人资料'}>
      <Tabs tabPosition="left">
        <Tabs.TabPane key="info" title="个人信息">
          <InfoPanel />
        </Tabs.TabPane>
        <Tabs.TabPane key="updatePassword" title="修改密码">
          <UpdatePasswordPanel />
        </Tabs.TabPane>
      </Tabs>
    </PageContainer>
  );
};
