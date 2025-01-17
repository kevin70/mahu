import { HAssetSelect } from '@/components/mart/HAssetSelect';
import { PlusOutlined } from '@ant-design/icons';
import {
  PageContainer,
  ProForm,
  ProFormCheckbox,
  ProFormRadio,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { css } from '@emotion/react';
import { Button, Card, Checkbox, Form, Tabs } from 'antd';

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

  const attributeVariant = (
    <>
      <div
        css={css`
          display: flex;
          justify-content: end;
        `}
      >
        <Button type="dashed" icon={<PlusOutlined />}>
          新增属性
        </Button>
      </div>
      <div>属性</div>
    </>
  );

  const variantPanel = (
    <>
      <Form.Item layout="horizontal" label={'多规格'} name={'multiple'}>
        <Checkbox />
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
              children: attributeVariant,
            },
            {
              key: 'variant',
              label: '规格信息',
              children: variantPanel,
            },
          ]}
        ></Tabs>
      </ProForm>
    </PageContainer>
  );
};
