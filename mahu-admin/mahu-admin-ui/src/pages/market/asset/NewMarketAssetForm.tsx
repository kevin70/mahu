import { HNewButton } from '@/components/HNewButton';
import { permits } from '@/config/permit';
import { MARKET_API, resolveApiError } from '@/services';
import { DrawerForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';

export const NewMarketAssetForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.MARKET_SHOP.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewMarketShopForm'],
    mutationFn(values: any) {
      return MARKET_API.addShop(values);
    },
    onSuccess() {
      $message().success('新增商店成功');
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
      title="新增商店"
      trigger={<HNewButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
    >
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
                const { items } = await MARKET_API.listShops(1, undefined, [`slug eq ${value}`], undefined, 1);
                const item = items && items.length > 0 ? items[0] : null;
                if (item) {
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
                const { items } = await MARKET_API.listShops(1, undefined, [`name eq ${value}`], undefined, 1);
                const item = items && items.length > 0 ? items[0] : null;
                if (item) {
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
