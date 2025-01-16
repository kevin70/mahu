import { HAssetSelect } from '@/components/mart/HAssetSelect';
import { PageContainer, ProForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { Flex, Form, Image, Tabs } from 'antd';

export const MartProductNew = () => {
  const basicPanel = (
    <>
      <ProFormText label={'名称'} name={'name'} />
      <ProFormTextArea label={'描述'} name={'description'} />
      <Form.Item name={'images'} label={'轮播图片'}>
        <HAssetSelect />
      </Form.Item>
      <Form.List name={'images'}>
        {(fields, { add, move, remove }) => {
          return (
            <Form.Item label={'图片'}>
              <Flex gap={'middle'}>
                <Image
                  width={100}
                  src={`https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png`}
                />
                <Image
                  width={100}
                  src={`https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png`}
                />
                <Image
                  width={100}
                  src={`https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png`}
                />
              </Flex>
            </Form.Item>
          );
        }}
      </Form.List>
    </>
  );

  return (
    <PageContainer title={'新建产品'}>
      <ProForm>
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
