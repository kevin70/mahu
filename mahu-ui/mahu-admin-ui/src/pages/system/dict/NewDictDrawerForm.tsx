import { HNewButton } from '@/components/HNewButton';
import { permits } from '@/config/permit';
import { resolveApiError, SYSTEM_API } from '@/services';
import { DrawerForm, ProFormDigit, ProFormItem, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { DictKindAutoComplete } from './DictKindAutoComplete';

export const NewDictDrawerForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.DICT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewDictDrawerForm'],
    mutationFn(values: any) {
      return SYSTEM_API.addDict(values);
    },
    onSuccess() {
      $message().success('新增字典成功');
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
      title="新增字典"
      trigger={<HNewButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
    >
      <ProFormText
        label="SLUG"
        name="slug"
        extra={'别名（SLUG）是网址的唯一标识部分，通常位于 URL 的末尾'}
        validateTrigger="onSubmit"
        rules={[
          { max: 32, message: '最长32个字符' },
          {
            async validator(_rule, value, _callback) {
              try {
                if (value) {
                  const { items } = await SYSTEM_API.listDicts({
                    limit: 1,
                    filter: `slug==${value}`,
                    noTotalCount: 0,
                  });
                  const exists = items && items.length > 0;
                  if (exists) {
                    return Promise.reject('字典 SLUG 已经存在');
                  }
                }
              } catch (_: any) {
                //
              }
              return Promise.resolve();
            },
          },
        ]}
      />
      <ProFormItem label="种类" name="kind" required rules={[{ required: true }]}>
        <DictKindAutoComplete />
      </ProFormItem>
      <ProFormTextArea label="值" required name="value" validateDebounce={1000} rules={[{ required: true }]} />
      <ProFormTextArea label="文本" required name="label" rules={[{ required: true }]} />
      <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
      <ProFormTextArea label="备注" name="remark" />
    </DrawerForm>
  );
};
