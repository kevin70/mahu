import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import {
  DrawerForm,
  ProForm,
  ProFormCheckbox,
  ProFormDigit,
  ProFormList,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { Card, Flex, message } from 'antd';
import { HEditButton } from '@/components/HEditButton';

export const EditDictDrawerForm = (props: { typeCode: string; onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.DICT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['EditDictDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.updateSystemDict({
        typeCode: props.typeCode,
        upsertDictRequest: values,
      });
    },
    onSuccess() {
      message.success('修改字典成功');
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
      title="编辑字典"
      trigger={<HEditButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
      request={() => {
        return SYSTEM_API.getSystemDict({ typeCode: props.typeCode });
      }}
    >
      <ProFormText
        label="类型代码"
        name="typeCode"
        validateTrigger="onSubmit"
        rules={[{ required: true }, { max: 32, message: '最长32个字符' }]}
        tooltip={'代码最长32位字符可使用字母、数字、下划线及英文句号'}
      />
      <ProFormText label="名称" required name="name" rules={[{ required: true }]} />
      <ProFormTextArea label="描述" name="description" />
      <ProFormCheckbox label="状态" name="status" tooltip={'未选中为禁用'}>
        启用
      </ProFormCheckbox>
      <ProFormList
        name={'data'}
        label={'字典数据'}
        alwaysShowItemLabel
        copyIconProps={false}
        itemRender={({ listDom, action }) => {
          return (
            <ProForm.Item>
              <Card size="small">
                <Flex justify="end">{action}</Flex>
                <div>{listDom}</div>
              </Card>
            </ProForm.Item>
          );
        }}
      >
        {() => {
          return (
            <>
              <ProFormText
                label="数据代码"
                required
                name={'dataCode'}
                rules={[{ required: true }]}
                tooltip={'代码最长32位字符可使用字母、数字、下划线及英文句号'}
              />
              <ProFormText label="数据名称" required name={'name'} rules={[{ required: true }]} />
              <ProFormTextArea label="数据值" required name={'value'} rules={[{ required: true }]} />
              <ProFormCheckbox label="状态" name="status" tooltip={'未选中为禁用'}>
                启用
              </ProFormCheckbox>
              <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
            </>
          );
        }}
      </ProFormList>
    </DrawerForm>
  );
};
