import { HNewButton } from '@/components/HNewButton';
import { DepartmentTreeSelect } from '@/components/system/DepartmentTreeSelect';
import { RoleSelect } from '@/components/system/RoleSelect';
import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormText } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { message } from 'antd';
import FormItem from 'antd/es/form/FormItem';

export const NewEmployeeDrawerForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.EMPLOYEE.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewEmployeeDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.addEmployee(values);
    },
    onSuccess() {
      message.success('新增职员成功');
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
      title="新增职员"
      trigger={<HNewButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
    >
      <ProFormText label="用户名" required name="username" rules={[{ required: true }]} />
      <ProFormText label="昵称" required name="nickname" rules={[{ required: true }]} />
      <ProFormText label="密码" required name="password" rules={[{ required: true }]} />
      <FormItem label="部门" name="departmentId">
        <DepartmentTreeSelect treeDefaultExpandAll />
      </FormItem>
      <RoleSelect label="角色" mode="multiple" name="roleIds" />
    </DrawerForm>
  );
};
