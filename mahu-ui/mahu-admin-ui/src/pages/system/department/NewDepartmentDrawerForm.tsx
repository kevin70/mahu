import { HNewButton } from '@/components/HNewButton';
import { DepartmentTreeSelect } from '@/components/system/DepartmentTreeSelect';
import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormDigit, ProFormText } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { message } from 'antd';
import FormItem from 'antd/es/form/FormItem';

export const NewDepartmentDrawerForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.DEPARTMENT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewDepartmentDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.createDepartment(values);
    },
    onSuccess() {
      message.success('新增部门成功');
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
      title="新增部门"
      trigger={<HNewButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
    >
      <ProFormText label="名称" required name="name" rules={[{ required: true }]} />
      <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
      <FormItem label="父部门" name="parentId">
        <DepartmentTreeSelect />
      </FormItem>
    </DrawerForm>
  );
};
