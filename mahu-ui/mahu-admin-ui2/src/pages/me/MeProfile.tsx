import { HSaveButton } from '@/components/HSaveButton';
import { PageContainer } from '@/components/PageContainer';
import { useProfileStore } from '@/stores';
import { Avatar, Form, Input, Tabs } from '@arco-design/web-react';
import { useMemo } from 'react';
import { useSearchParams } from 'react-router';
import { useShallow } from 'zustand/shallow';

export const MeProfile = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const activeTab = useMemo(() => searchParams.get('kind') || 'info', [searchParams]);

  const InfoPanel = () => {
    const { nickname, avatar } = useProfileStore(
      useShallow((state) => ({ nickname: state.nickname, avatar: state.avatar }))
    );

    return (
      <Form layout="vertical" initialValues={{ nickname, avatar }}>
        <Form.Item field={'avatar'} label="头像">
          <Avatar size={64}>
            <img src={avatar} />
          </Avatar>
        </Form.Item>
        <Form.Item field={'nickname'} label="昵称" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <HSaveButton long />
      </Form>
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
      <Tabs
        tabPosition="left"
        activeTab={activeTab}
        onClickTab={(key) => {
          setSearchParams((prev) => {
            return { ...prev, kind: key };
          });
        }}
      >
        <Tabs.TabPane key="info" title="个人信息">
          <InfoPanel />
        </Tabs.TabPane>
        <Tabs.TabPane key="update_password" title="修改密码">
          <UpdatePasswordPanel />
        </Tabs.TabPane>
      </Tabs>
    </PageContainer>
  );
};
