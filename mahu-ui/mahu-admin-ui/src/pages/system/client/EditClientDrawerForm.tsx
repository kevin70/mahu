import { HEditButton } from '@/components/HEditButton';
import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormSelect, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { message } from 'antd';
import { FormInstance } from 'antd/lib';

export const EditClientDrawerForm = (props: { clientId: string; onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.CLIENT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['EditClientDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.updateClient(values, values.clientId);
    },
    onSuccess() {
      message.success('修改认证终端成功');
      props.onSuccess();
    },
    async onError(error) {
      const err = await resolveApiError(error);
      message.error(err.message);
    },
  });

  const onInit = async (_: any, form: FormInstance<any>) => {
    const data = await SYSTEM_API.getClient({ clientId: props.clientId });
    form.setFieldsValue(data);
  };

  return (
    <DrawerForm
      drawerProps={{
        destroyOnClose: true,
        onClose() {
          reset();
        },
      }}
      title="修改认证终端"
      trigger={<HEditButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
      onInit={onInit}
    >
      <ProFormText label="客户端 ID" disabled name="clientId" />
      <ProFormText label="标签" required name="label" rules={[{ required: true }]} />
      <ProFormTextArea label="备注" name="remark" />
      <ProFormSelect
        label="终端类型"
        required
        name="terminalType"
        options={[
          {
            value: 'WECHAT_XCX',
            label: '微信小程序',
          },
          {
            value: 'WECHAT_WEB',
            label: '微信网页授权',
          },
          {
            value: 'ADMIN_BROWSER_PASSWORD',
            label: '管理员浏览器密码登录',
          },
        ]}
        rules={[{ required: true }]}
      />
      <ProFormText label="微信 APPID" name="wechatAppid" />
      <ProFormTextArea label="微信 APP 密钥" name="wechatAppsecret" />
    </DrawerForm>
  );
};
