import { HAssetMultipleSelect, HAssetSelect } from '@/components/mart/HAssetSelect';
import { HAttributeChoose } from '@/components/mart/HAttributeChoose';
import { PlusOutlined } from '@ant-design/icons';
import {
  FooterToolbar,
  PageContainer,
  ProForm,
  ProFormDigit,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { css } from '@emotion/react';
import { Button, Card, Checkbox, Col, Form, Row, Table } from 'antd';
import { AttributeFormItem } from './AttributeFormItem';
import { useDynamicList } from 'ahooks';

export const MartProductNew = () => {
  const attributeList = useDynamicList<number>([]);
  const variantAttributeList = useDynamicList<number>([]);

  const basicPanel = (
    <>
      <ProFormText label={'名称'} name={'name'} rules={[{ required: true }]} />
      <ProFormTextArea label={'描述'} name={'description'} />
      <Form.Item name={'images'} label={'轮播图片'} rules={[{ required: true }]}>
        <HAssetMultipleSelect />
      </Form.Item>
    </>
  );

  const attributePanel = (
    <Form.List name={'attributes'}>
      {() => (
        <Row gutter={[16, 16]}>
          {attributeList.list.map((attributeId, i) => (
            <Col span={12}>
              <div
                css={css`
                  display: none;
                `}
              >
                <ProFormDigit name={[attributeList.getKey(i), 'attributeId']} initialValue={attributeId} />
              </div>
              <AttributeFormItem key={i} name={[attributeList.getKey(i)]} attributeId={attributeId} />
            </Col>
          ))}
        </Row>
      )}
    </Form.List>
  );

  const variantPanel = (
    <>
      <Table>
        <Table.Column
          title="封面"
          dataIndex={'cover'}
          render={(row) => {
            return <></>;
          }}
        />
        <Table.Column title="长" dataIndex={'length'} />
        <Table.Column title="宽" dataIndex={'width'} />
        <Table.Column title="高" dataIndex={'height'} />
        <Table.Column title="重量" dataIndex={'weight'} />
        <Table.Column align="right" />
      </Table>

      <Form.Item layout="horizontal" label={'多规格'} name={'multiple'}>
        <Checkbox />
      </Form.Item>
      <Form.Item noStyle>
        <HAssetSelect />
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
        submitter={{
          render: (_, dom) => <FooterToolbar>{dom}</FooterToolbar>,
        }}
        onFinish={async (values) => {
          await submit(values);
        }}
        css={css`
          display: flex;
          flex-direction: column;
          row-gap: var(--ant-margin);
        `}
      >
        <Card title="基本信息">{basicPanel}</Card>

        <Card
          title="属性信息"
          extra={
            <HAttributeChoose
              trigger={
                <Button type="dashed" icon={<PlusOutlined />}>
                  新增属性
                </Button>
              }
              onChange={(value) => {
                for (const v of value) {
                  attributeList.push(v);
                }
              }}
            />
          }
        >
          {attributePanel}
        </Card>

        <Card
          title="规格"
          extra={
            <HAttributeChoose
              trigger={
                <Button type="dashed" icon={<PlusOutlined />}>
                  新增规格属性
                </Button>
              }
              onChange={(value) => {
                for (const v of value) {
                  variantAttributeList.push(v);
                }
              }}
            />
          }
        >
          {variantPanel}
        </Card>
      </ProForm>
    </PageContainer>
  );
};
