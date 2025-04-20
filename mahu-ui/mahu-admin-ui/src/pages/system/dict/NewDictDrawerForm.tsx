import { HNewButton } from '@/components/HNewButton';
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

export const NewDictDrawerForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.DICT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewDictDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.createSystemDict({ upsertDictRequest: values });
    },
    onSuccess() {
      message.success('新增字典成功');
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
      title="新增字典"
      trigger={<HNewButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
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
      <ProFormCheckbox label="状态" name="disabled" tooltip={'选中为禁用'}>
        禁用
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
              <ProFormCheckbox label="状态" name="disabled" tooltip={'选中为禁用'}>
                禁用
              </ProFormCheckbox>
              <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
            </>
          );
        }}
      </ProFormList>
    </DrawerForm>
  );
};
