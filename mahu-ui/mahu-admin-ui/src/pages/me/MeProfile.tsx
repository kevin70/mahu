import { BASE_API, ME_API, resolveApiError } from '@/services';
import { useProfileStore } from '@/stores';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { PageContainer, ProCard, ProForm, ProFormText } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { Col, Form, message, Row, Upload } from 'antd';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router';

export const MeProfile = () => {
  const { kind } = useParams();
  const navigate = useNavigate();
  const profileStore = useProfileStore();

  const ProfileCard = () => {
    const { mutateAsync, isPending } = useMutation<any>({
      mutationKey: ['MeProfile'],
      mutationFn(values: any) {
        return ME_API.updateMeProfile(values);
      },
      onSuccess() {
        profileStore.refreshProfile();
        message.success('个人信息修改成功');
      },
      async onError(error) {
        const err = await resolveApiError(error);
        message.error(err.message);
      },
    });

    const AvatarUpload = ({ value }: { value?: string; onChange?: (v: string) => void }) => {
      const [loading, setLoading] = useState(false);
      const uploadButton = (
        <button style={{ border: 0, background: 'none' }} type="button">
          {loading ? <LoadingOutlined /> : <PlusOutlined />}
          <div style={{ marginTop: 8 }}>上传</div>
        </button>
      );

      return (
        <Upload
          listType="picture-circle"
          showUploadList={false}
          beforeUpload={(file) => {
            // 限制文件类型
            const allowedTypes = ['image/jpeg', 'image/png'];
            if (!allowedTypes.includes(file.type)) {
              message.error('头像只能上传JPG/PNG文件');
              return false;
            }

            // 限制文件大小MB
            const sizeLimit = 2;
            if (file.size > sizeLimit * 1024 * 1024) {
              message.error(`图片大小不能超过${sizeLimit}MB`);
              return false;
            }
            return true;
          }}
          customRequest={async (options) => {
            const { presignedUploadUrl } = await BASE_API.getPresignedUpload({
              getPresignedUploadRequest: {
                kind: 'ADMIN_AVATAR',
                fileName: (options.file as any).name,
              },
            });

            const response = await fetch(presignedUploadUrl, {
              method: 'PUT',
              body: options.file, // 直接上传整个文件
            });
            if (!response.ok) {
              const data = await response.json();
              console.log('文件上传响应', data);
              // options.onSuccess();
            }

            console.log('File uploaded successfully');
          }}
        >
          {value ? <img src={value} alt="用户头像" style={{ width: '100%' }} /> : uploadButton}
        </Upload>
      );
    };

    return (
      <ProCard>
        <ProForm
          loading={isPending}
          request={async () => ME_API.getMeProfile()}
          onFinish={async (values: any) => {
            await mutateAsync(values);
            return true;
          }}
        >
          <Row gutter={36}>
            <Col span={8}>
              <ProFormText label="昵称" name="nickname" />
            </Col>
            <Col span={8}>
              <Form.Item label="头像" name="avatar">
                <AvatarUpload />
              </Form.Item>
            </Col>
          </Row>
        </ProForm>
      </ProCard>
    );
  };

  const ChangePasswordCard = () => {
    const { mutateAsync, isPending } = useMutation<any>({
      mutationKey: ['MeChangePassword'],
      mutationFn(values: any) {
        return ME_API.updateMePassword(values);
      },
      onSuccess() {
        message.success('密码修改成功，请重新登录', 1, () => {
          $gotoLogin();
        });
      },
      async onError(error) {
        const err = await resolveApiError(error);
        message.error(err.message);
      },
    });

    return (
      <ProCard>
        <ProForm
          loading={isPending}
          onFinish={async (values: any) => {
            await mutateAsync(values);
            return true;
          }}
        >
          <ProFormText.Password label="原始密码" name="originalPassword" required rules={[{ required: true }]} />
          <ProFormText.Password
            label="新密码"
            name="password"
            required
            rules={[
              { required: true },
              {
                min: 6,
                message: '密码长度最少6位',
              },
            ]}
          />
          <ProFormText.Password
            label="确认密码"
            name="confirmPassword"
            required
            dependencies={['password']}
            rules={[
              { required: true },
              ({ getFieldValue }) => ({
                validator(_, value, _callback) {
                  if (value && getFieldValue('password') !== value) {
                    return Promise.reject('新密码与确认密码不匹配');
                  }
                  return Promise.resolve();
                },
              }),
            ]}
          />
        </ProForm>
      </ProCard>
    );
  };

  return (
    <PageContainer
      title="个人信息"
      tabActiveKey={kind}
      onTabChange={(k) => {
        navigate(`/me/${k}`);
      }}
      tabList={[
        {
          tab: '基本信息',
          key: 'profile',
          children: <ProfileCard />,
        },
        {
          tab: '修改密码',
          key: 'change-password',
          children: <ChangePasswordCard />,
        },
      ]}
    ></PageContainer>
  );
};
