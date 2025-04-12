import { HNewButton } from '@/components/HNewButton';
import { permits } from '@/config/permit';
import { RSQL_OPS } from '@/hooks';
import { BASE_API, resolveApiError, uploadFile } from '@/services';
import { DrawerForm, ProFormDigit, ProFormText, ProFormUploadButton } from '@ant-design/pro-components';
import { useMutation } from '@tanstack/react-query';
import { message } from 'antd';

export const NewBrandDrawerForm = (props: { onSuccess: () => void }) => {
  const noWrite = $checkNotPermit(permits.DICT.W);
  const { mutateAsync, reset } = useMutation<any>({
    mutationKey: ['NewBrandDrawerForm'],
    mutationFn(values: any) {
      const { logoFiles, ...otherProps } = values;
      const body: any = {
        ...otherProps,
      };
      if (logoFiles && logoFiles.length > 0) {
        body.logo = logoFiles[0].url;
      }
      return BASE_API.addBrand(body);
    },
    onSuccess() {
      message.success('新增品牌成功');
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
                  const { items } = await BASE_API.listBrands({
                    limit: 1,
                    noTotalCount: 1,
                    filter: RSQL_OPS.encode(RSQL_OPS.eq('name', value)),
                  });
                  const item = items && items.length > 0 ? items[0] : null;
                  if (item) {
                    return Promise.reject('品牌已经存在');
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
        extra="品牌名称的首字母"
      />
      <ProFormUploadButton
        label="LOGO"
        name="logoFiles"
        max={1}
        accept=".jpg,.jpeg,.png"
        fieldProps={{
          multiple: false,
          name: 'file',
          listType: 'picture-card',
          showUploadList: {
            showPreviewIcon: false,
          },
          async customRequest(options) {
            try {
              // @ts-ignore
              const fileName = options.file.name;
              const policy = await BASE_API.makeOssDirectUpload({
                makeOssDirectUploadRequest: { kind: 'BRAND', fileName: fileName },
              });
              const file: any = options.file;

              await uploadFile(
                policy.endpoint,
                {
                  key: policy.key,
                  policy: policy.policy,
                  accessKeyId: policy.accessKeyId,
                  signature: policy.signature,
                  file,
                },
                (event) => {
                  // @ts-ignore
                  options.onProgress?.({ percent: Math.round((event.loaded / event.total) * 100) }, file);
                }
              );

              file.url = policy.accessUrl;
              options.onSuccess?.(policy.accessUrl);
            } catch (e) {
              // @ts-ignore
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
