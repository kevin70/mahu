import { HAssetMultipleSelect, HAssetSelect } from '@/components/mart/HAssetSelect';
import { HAttributeChoose } from '@/components/mart/HAttributeChoose';
import { CloseOutlined, MinusOutlined, PlusOutlined } from '@ant-design/icons';
import {
  FooterToolbar,
  PageContainer,
  ProForm,
  ProFormDigit,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { css } from '@emotion/react';
import { Button, Card, Checkbox, Col, Form, Input, Row, Space, Table, Tag, Typography } from 'antd';
import { AttributeFormItem } from './AttributeFormItem';
import { useDynamicList } from 'ahooks';
import { AttributeName } from './AttributeName';
import { VariantAttributeColumn } from './VariantAttributeColumn';
import { useMartAttributeData } from '@/hooks';

export const MartProductNew = () => {
  const attributeList = useDynamicList<number>([]);
  const variantAttributeList = useDynamicList<number>([]);

  const variants = useDynamicList<{
    cover: string;
  }>([
    {
      cover: 'abcdefg',
    },
  ]);

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
      <Table dataSource={variants.list}>
        <Table.Column
          title="封面"
          render={() => {
            return (
              <Form.Item noStyle>
                <HAssetSelect width={48} height={48} />
              </Form.Item>
            );
          }}
        />

        {/* ============= 规格属性 ============= */}
        {variantAttributeList.list.map((o, i) => {
          return (
            <Table.Column
              key={i}
              title={
                <Space>
                  <AttributeName attributeId={o} />
                  <Button
                    size="small"
                    variant="text"
                    color="danger"
                    icon={<CloseOutlined />}
                    onClick={(e) => {
                      e.preventDefault();
                      variantAttributeList.remove(i);
                    }}
                  />
                </Space>
              }
              render={() => {
                return <AttributeFormItem name={[i]} attributeId={o} noStyle />;
              }}
            />
          );
        })}
        {/* ============= 规格属性 ============= */}

        <Table.Column
          title="长"
          render={(_row, _, i) => {
            return <Input placeholder="长" />;
          }}
        />
        <Table.Column
          title="宽"
          render={(_row, _, i) => {
            return <Input placeholder="长" />;
          }}
        />
        <Table.Column
          title="高"
          render={(_row, _, i) => {
            return <Input placeholder="长" />;
          }}
        />
        <Table.Column
          title="重量"
          render={(_row, _, i) => {
            return <Input placeholder="长" />;
          }}
        />
        <Table.Column
          align="right"
          render={(_row, _, i) => {
            return <Button variant="text" color="danger" icon={<CloseOutlined />}></Button>;
          }}
        />
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
          title="产品属性"
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
          title={<Typography.Text strong>规格</Typography.Text>}
          extra={
            <HAttributeChoose
              trigger={
                <Space>
                  <Button icon={<PlusOutlined />}>规格</Button>
                  <Button type="dashed" icon={<PlusOutlined />}>
                    新增属性
                  </Button>
                </Space>
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
