import { HAssetSelect } from '@/components/mart/HAssetSelect';
import { HAttributeChoose } from '@/components/mart/HAttributeChoose';
import { PlusOutlined } from '@ant-design/icons';
import { PageContainer, ProForm, ProFormDigit, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { css } from '@emotion/react';
import { Button, Checkbox, Col, Form, Row, Tabs } from 'antd';
import { AttributeFormItem } from './AttributeFormItem';
import { useDynamicList, useSet } from 'ahooks';
import FormItem from 'antd/es/form/FormItem';

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

  const [attributeIds, attributeIdsOpts] = useSet<number>([]);

  const AttributeVariant = () => {
    const { list, getKey, push, remove } = useDynamicList<number>([]);
    return (
      <Form.List name={'attributes'}>
        {(fields, { add, remove }) => (
          <Row gutter={[16, 16]}>
            {list.map((attributeId, i) => (
              <Col span={12}>
                <div
                  css={css`
                    display: none;
                  `}
                >
                  <ProFormDigit name={[getKey(i), 'attributeId']} initialValue={attributeId} />
                </div>
                <AttributeFormItem key={i} name={[getKey(i)]} attributeId={attributeId} />
              </Col>
            ))}

            <HAttributeChoose
              trigger={
                <Button type="dashed" icon={<PlusOutlined />}>
                  新增属性
                </Button>
              }
              onChange={(value) => {
                for (const v of value) {
                  push(v);
                }
              }}
            />
          </Row>
        )}
      </Form.List>
    );
  };

  const variantPanel = (
    <>
      <Form.Item layout="horizontal" label={'多规格'} name={'multiple'}>
        <Checkbox />
      </Form.Item>
      <Form.Item>
        <HAttributeChoose trigger={<Button>新增</Button>} />
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
              children: <AttributeVariant />,
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
