import { HEditButton } from '@/components/HEditButton';
import { DepartmentTreeSelect } from '@/components/system/DepartmentTreeSelect';
import { AdminStatusSelect } from '@/components/system/AdminStatusSelect';
import { RoleSelect } from '@/components/system/RoleSelect';
import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormText } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { Form, FormInstance, Input, message } from 'antd';
import FormItem from 'antd/es/form/FormItem';

export const EditAdminDrawerForm = (props: { id: number; onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.ADMIN.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['EditAdminDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.updateAdmin({
        id: props.id,
        upsertAdminRequest: values,
      });
    },
    onSuccess() {
      message.success('修改职员成功');
      props.onSuccess();
    },
    async onError(error) {
      const err = await resolveApiError(error);
      message.error(err.message);
    },
  });

  const onInit = async (_: any, form: FormInstance<any>) => {
    const data = await SYSTEM_API.getAdmin({ id: props.id });
    form.setFieldsValue(data);
    form.setFieldValue('departmentId', data.department?.id);
  };

  return (
    <DrawerForm
      drawerProps={{
        destroyOnClose: true,
        onClose() {
          reset();
        },
      }}
      title="修改管理员"
      trigger={<HEditButton disabled={noWrite || props.id === 1} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
      onInit={onInit}
    >
      <FormItem label="ID">
        <Input disabled value={props.id} />
      </FormItem>
      <ProFormText label="用户名" required name="username" rules={[{ required: true }]} />
      <ProFormText label="昵称" required name="nickname" rules={[{ required: true }]} />
      <ProFormText.Password
        label="密码"
        name="password"
        placeholder={'输入重置选手密码'}
        extra={'如果输入值则会重置职员的登录密码'}
      />
      <Form.Item label="状态" name="status" required>
        <AdminStatusSelect placeholder="请选择" />
      </Form.Item>
      <FormItem label="部门" name="departmentId">
        <DepartmentTreeSelect treeDefaultExpandAll />
      </FormItem>
      <RoleSelect label="角色" mode="multiple" name="roleIds" />
    </DrawerForm>
  );
};
