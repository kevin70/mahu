import { permits } from '@/config/permit';
import { RSQL_OPS } from '@/hooks';
import { MARKET_API, resolveApiError } from '@/services';
import { EditOutlined } from '@ant-design/icons';
import { DrawerForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { Button, Form, FormInstance, Input, message } from 'antd';

export const EditMarketShopForm = (props: { id: number; onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.MARKET_SHOP.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['EditMarketShopForm'],
    mutationFn(values: any) {
      return MARKET_API.updateShop({
        id: props.id,
        upsertShopRequest: values,
      });
    },
    onSuccess() {
      message.success('编辑门店成功');
      props.onSuccess();
    },
    async onError(error) {
      const err = await resolveApiError(error);
      message.error(err.message);
    },
  });

  const onInit = async (_: any, form: FormInstance<any>) => {
    const data = await MARKET_API.getShop({ id: props.id });
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
      title="编辑门店"
      trigger={<Button color="default" variant="link" icon={<EditOutlined />} disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
      onInit={onInit}
    >
      <Form.Item label="ID">
        <Input disabled value={props.id} />
      </Form.Item>
      <ProFormText
        label="SLUG"
        name="slug"
        required
        validateTrigger="onSubmit"
        rules={[
          { required: true },
          { max: 8, message: '最长8个字符' },
          { pattern: /^[A-Za-z0-9]+$/, message: '只能输入字母和数字' },
          {
            async validator(_rule, value, _callback) {
              if (value) {
                const { items } = await MARKET_API.listShops({
                  limit: 1,
                  filter: RSQL_OPS.encode(RSQL_OPS.eq('slug', value)),
                  noTotalCount: 1,
                });
                const item = items && items.length > 0 ? items[0] : null;
                if (item && item.id !== props.id) {
                  return Promise.reject('门店 SLUG 已经存在');
                }
              }
              return Promise.resolve();
            },
          },
        ]}
      />
      <ProFormText
        label="名称"
        name="name"
        required
        validateTrigger="onSubmit"
        rules={[
          { required: true },
          { max: 32, message: '最长32个字符' },
          {
            async validator(_rule, value, _callback) {
              if (value) {
                const { items } = await MARKET_API.listShops({
                  limit: 1,
                  filter: RSQL_OPS.encode(RSQL_OPS.eq('name', value)),
                  noTotalCount: 1,
                });
                const item = items && items.length > 0 ? items[0] : null;
                if (item && item.id !== props.id) {
                  return Promise.reject('门店已经存在');
                }
              }
              return Promise.resolve();
            },
          },
        ]}
      />
      <ProFormTextArea label="描述" name="description" />
    </DrawerForm>
  );
};
