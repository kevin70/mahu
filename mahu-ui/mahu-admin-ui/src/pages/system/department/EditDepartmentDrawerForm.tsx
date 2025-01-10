import { HEditButton } from '@/components/HEditButton';
import { DepartmentTreeSelect } from '@/components/system/DepartmentTreeSelect';
import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormDigit, ProFormText } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { FormInstance, Input, message } from 'antd';
import FormItem from 'antd/es/form/FormItem';

export const EditDepartmentDrawerForm = (props: { id: number; onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.DEPARTMENT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['EditDepartmentDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.updateDepartment({
        id: props.id,
        upsertDepartmentRequest: values,
      });
    },
    onSuccess() {
      message.success('修改部门成功');
      props.onSuccess();
    },
    async onError(error) {
      const err = await resolveApiError(error);
      message.error(err.message);
    },
  });

  const onInit = async (_: any, form: FormInstance<any>) => {
    const data = await SYSTEM_API.getDepartment({ id: props.id });
    form.setFieldsValue(data);
    form.setFieldValue('parentId', data.parent?.id);
  };

  return (
    <DrawerForm
      drawerProps={{
        destroyOnClose: true,
        onClose() {
          reset();
        },
      }}
      title="编辑部门"
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
      <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
      <FormItem label="父部门" name="parentId">
        <DepartmentTreeSelect nonSelectIds={[props.id]} />
      </FormItem>
    </DrawerForm>
  );
};
