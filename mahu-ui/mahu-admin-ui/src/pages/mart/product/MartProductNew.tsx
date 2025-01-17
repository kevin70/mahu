import { HAssetSelect } from '@/components/mart/HAssetSelect';
import { PageContainer, ProForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { Form, Tabs } from 'antd';

export const MartProductNew = () => {
  const basicPanel = (
    <>
      <ProFormText label={'名称'} name={'name'} />
      <ProFormTextArea label={'描述'} name={'description'} />
      <Form.Item name={'images'} label={'轮播图片'}>
        <HAssetSelect />
      </Form.Item>
    </>
  );

  const submit = async (values: any) => {
    console.log('new product', values);
  };

  return (
    <PageContainer title={'新建产品'}>
      <ProForm
        onFinish={async (values) => {
          await submit(values);
        }}
      >
        <Tabs
          items={[
            {
              key: 'basic',
              label: '基本信息',
              children: basicPanel,
            },
            {
              key: 'attribute',
              label: '属性信息',
              children: basicPanel,
            },
            {
              key: 'variant',
              label: '规格信息',
              children: basicPanel,
            },
          ]}
        ></Tabs>
      </ProForm>
    </PageContainer>
  );
};
