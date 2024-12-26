import { HNewButton } from '@/components/HNewButton';
import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormSelect, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';

export const NewClientDrawerForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.CLIENT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewClientDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.addClient(values);
    },
    onSuccess() {
      $message().success('新增认证终端成功');
      props.onSuccess();
    },
    async onError(error) {
      const err = await resolveApiError(error);
      $message().error(err.message);
    },
  });

  return (
    <DrawerForm
      drawerProps={{
        destroyOnClose: true,
        onClose() {
          reset();
        },
      }}
      title="新增认证终端"
      trigger={<HNewButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
    >
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
