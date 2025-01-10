import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormDigit, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { Input, message } from 'antd';
import FormItem from 'antd/es/form/FormItem';
import { FormInstance } from 'antd/lib';
import { PermitTransfer } from './PermitTransfer';
import { HEditButton } from '@/components/HEditButton';

export const EditRoleDrawerForm = (props: { id: number; onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.CLIENT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['EditRoleDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.updateRole({
        id: props.id,
        upsertRoleRequest: values,
      });
    },
    onSuccess() {
      message.success('修改角色成功');
      props.onSuccess();
    },
    async onError(error) {
      const err = await resolveApiError(error);
      message.error(err.message);
    },
  });

  const onInit = async (_: any, form: FormInstance<any>) => {
    const data = await SYSTEM_API.getRole({ id: props.id });
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
      title="修改角色"
      trigger={<HEditButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
      onInit={onInit}
    >
      <FormItem label="ID">
        <Input disabled value={props.id} />
      </FormItem>
      <ProFormText label="名称" required name="name" rules={[{ required: true }]} />
      <ProFormTextArea label="备注" name="remark" />
      <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
      <FormItem label="权限" name="permits" required rules={[{ required: true }]}>
        <PermitTransfer />
      </FormItem>
    </DrawerForm>
  );
};
