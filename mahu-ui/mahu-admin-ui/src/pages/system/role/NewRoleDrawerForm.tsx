import { HNewButton } from '@/components/HNewButton';
import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormDigit, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import FormItem from 'antd/es/form/FormItem';
import { PermitSelect } from './PermitSelect';
import { message } from 'antd';

export const NewRoleDrawerForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.CLIENT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewRoleDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.createRole({
        upsertRoleRequest: values,
      });
    },
    onSuccess() {
      message.success('新增角色成功');
      props.onSuccess();
    },
    async onError(error) {
      const err = await resolveApiError(error);
      message.error(err.message);
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
      title="新增角色"
      trigger={<HNewButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
    >
      <ProFormDigit label="ID" required name="id" rules={[{ required: true }]} />
      <ProFormText label="名称" required name="name" rules={[{ required: true }]} />
      <ProFormTextArea label="备注" name="remark" />
      <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
      <FormItem label="权限" name="permits" required rules={[{ required: true, message: '至少选择一个权限' }]}>
        <PermitSelect />
      </FormItem>
    </DrawerForm>
  );
};
