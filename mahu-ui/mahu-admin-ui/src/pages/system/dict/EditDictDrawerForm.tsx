import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { EditOutlined } from '@ant-design/icons';
import { DrawerForm, ProFormDigit, ProFormItem, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { Button, Form, Input } from 'antd';
import { DictKindAutoComplete } from './DictKindAutoComplete';

export const EditDictDrawerForm = (props: { id: number; onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.DICT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['EditDictDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.updateDict(props.id, values);
    },
    onSuccess() {
      $message().success('修改字典成功');
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
      title="编辑字典"
      trigger={<Button color="default" variant="link" icon={<EditOutlined />} disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
      request={() => {
        return SYSTEM_API.getDict(props.id);
      }}
    >
      <Form.Item label="ID">
        <Input disabled value={props.id} />
      </Form.Item>
      <ProFormText label="SLUG" name="slug" disabled />
      <ProFormItem label="种类" name="kind" required rules={[{ required: true }]}>
        <DictKindAutoComplete />
      </ProFormItem>
      <ProFormTextArea label="值" required name="value" rules={[{ required: true }]} />
      <ProFormTextArea label="文本" required name="label" rules={[{ required: true }]} />
      <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
      <ProFormTextArea label="备注" name="remark" />
    </DrawerForm>
  );
};
