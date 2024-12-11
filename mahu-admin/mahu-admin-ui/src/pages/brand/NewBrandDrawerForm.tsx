import { HNewButton } from '@/components/HNewButton';
import { permits } from '@/config/permit';
import { BASIS_API, resolveApiError, SYSTEM_API, uploadFile } from '@/services';
import { DrawerForm, ProFormDigit, ProFormText, ProFormUploadButton } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';

export const NewBrandDrawerForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.DICT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewBrandDrawerForm'],
    mutationFn(values: any) {
      return BASIS_API.addBrand(values);
    },
    onSuccess() {
      $message().success('新增品牌成功');
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
      title="新增品牌"
      trigger={<HNewButton disabled={noWrite} />}
      onFinish={async (values: any) => {
        await mutateAsync(values);
        return true;
      }}
    >
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
              try {
                if (value) {
                  const { items } = await SYSTEM_API.listDicts(1, undefined, [`slug eq ${value}`], undefined, 1);
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
      <ProFormText
        label="首字母"
        name="firstLetter"
        required
        rules={[
          { required: true },
          { max: 1, message: '只能输入一个字母' },
          { pattern: /^[A-Z]{1,1}$/, message: '只能输入A-Z中的字母' },
        ]}
      />
      <ProFormUploadButton
        label="LOGO"
        name="logo"
        max={1}
        fieldProps={{
          multiple: false,
          name: 'file',
          listType: 'picture-card',
          async customRequest(options) {
            const policy = await BASIS_API.makeOssDirectUpload({ kind: 'BRAND', fileName: options.filename! });
            try {
              await uploadFile(
                policy.endpoint,
                {
                  key: policy.key,
                  policy: policy.policy,
                  accessKeyId: policy.accessKeyId,
                  signature: policy.signature,
                  file: options.file,
                },
                (event) => {
                  //
                }
              );
            } catch (e) {
              options.onError?.(e);
            }
          },
        }}
        extra="品牌LOGO"
      />
      <ProFormDigit label="排序" required name="ordering" initialValue={1} rules={[{ required: true }]} />
    </DrawerForm>
  );
};
