import { HAssetMultipleSelect, HAssetSelect } from '@/components/mart/HAssetSelect';
import { HAttributeChoose } from '@/components/mart/HAttributeChoose';
import { PlusOutlined } from '@ant-design/icons';
import {
  FooterToolbar,
  PageContainer,
  ProForm,
  ProFormDigit,
  ProFormRadio,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { Button, Card, Col, Divider, Form, message, Row, Space, Table } from 'antd';
import { AttributeFormItem } from './AttributeFormItem';
import { useDynamicList, useSet } from 'ahooks';
import { useForm } from 'antd/es/form/Form';
import { HCloseButton } from '@/components/HCloseButton';
import { HMartAttributeMeta } from '@/components/mart/HMartAttributeMeta';
import { BatchSetNumber } from './BatchSetNumber';
import { ProductTypeEnum } from '@/services/generated';
import { MART_API } from '@/services';
import { css } from '@styled-system/css';
import { useState } from 'react';

export const MartProductNew = () => {
  const attributeList = useDynamicList<number>([]);
  const variantAttributeList = useDynamicList<number>([]);
  const [form] = useForm();

  // 变体的限定名称
  const [variantErrors, variantErrorOpts] = useSet<number>([]);

  const basicPanel = (
    <Card title="产品信息">
      <ProFormText label={'名称'} name={'name'} rules={[{ required: true }]} />
      <ProFormTextArea label={'描述'} name={'description'} />
      <ProFormRadio.Group
        label={'类型'}
        options={[
          { label: '实体商品', value: ProductTypeEnum.Physical },
          { label: '虚拟商品', value: ProductTypeEnum.Virtual },
        ]}
        name={'type'}
        rules={[{ required: true }]}
      />
      <Form.Item name={'images'} label={'轮播图片'} rules={[{ required: true }]}>
        <HAssetMultipleSelect />
      </Form.Item>
    </Card>
  );

  const attributePanel = (
    <Form.List name={'attributes'}>
      {(_, { add, remove }) => (
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
                  add({});
                }
              }}
            />
          }
        >
          <Row gutter={[48, 16]}>
            {attributeList.list.map((attributeId, i) => (
              <Col span={12}>
                <AttributeFormItem
                  key={i}
                  name={[i]}
                  attributeId={attributeId}
                  onLoaded={(data) => {
                    form.setFieldValue(['attributes', i, 'attributeId'], data.id);
                    form.setFieldValue(['attributes', i, 'valueType'], data.valueType);
                  }}
                  label={
                    <HCloseButton
                      className={css`
                        margin-left: var(--ant-margin);
                      `}
                      onClick={() => {
                        attributeList.remove(i);
                        remove(i);
                      }}
                    />
                  }
                />
              </Col>
            ))}
          </Row>
        </Card>
      )}
    </Form.List>
  );

  const variantPanel = (
    <Form.List name={'variants'}>
      {(variants, { add, remove }) => (
        <Card
          title="产品规格"
          extra={
            <Space>
              <div>批量设置:</div>
              <BatchSetNumber
                label="价格"
                onChange={(v) => {
                  variants.forEach((field) => {
                    form.setFieldValue(['variants', field.name, 'price'], v);
                  });
                }}
              />
              <BatchSetNumber
                label="长度"
                onChange={(v) => {
                  variants.forEach((field) => {
                    form.setFieldValue(['variants', field.name, 'length'], v);
                  });
                }}
              />
              <BatchSetNumber
                label="宽度"
                onChange={(v) => {
                  variants.forEach((field) => {
                    form.setFieldValue(['variants', field.name, 'width'], v);
                  });
                }}
              />
              <BatchSetNumber
                label="高度"
                onChange={(v) => {
                  variants.forEach((field) => {
                    form.setFieldValue(['variants', field.name, 'height'], v);
                  });
                }}
              />
              <BatchSetNumber
                label="重量"
                onChange={(v) => {
                  variants.forEach((field) => {
                    form.setFieldValue(['variants', field.name, 'weight'], v);
                  });
                }}
              />

              <Divider type="vertical" />

              <Button
                icon={<PlusOutlined />}
                onClick={() => {
                  add({});
                }}
              >
                规格
              </Button>

              <HAttributeChoose
                trigger={
                  <Button type="dashed" icon={<PlusOutlined />} disabled={variantAttributeList.list.length >= 3}>
                    新增属性
                  </Button>
                }
                onChange={(value) => {
                  if (value.length <= 0) {
                    return;
                  }

                  const addValues: number[] = [];
                  for (const v of value) {
                    // 规格属性最多只能存在3个
                    if (variantAttributeList.list.length >= 3) {
                      break;
                    }
                    if (!variantAttributeList.list.includes(v)) {
                      variantAttributeList.push(v);
                      addValues.push(v);
                    }
                  }

                  if (addValues.length > 0) {
                    variants.forEach((variant) => {
                      const names = [variant.name, 'attributes'];
                      const v = form.getFieldValue(names) || [];
                      form.setFieldValue(names, [...v, addValues.map((attributeId) => ({ attributeId }))]);
                    });
                  }
                }}
              />
            </Space>
          }
        >
          <Table
            dataSource={variants}
            pagination={false}
            rowKey={'key'}
            summary={() => {
              if (variantErrors.size === 0) {
                return <></>;
              }

              return (
                <div
                  className={css`
                    color: var(--ant-color-error);
                    font-weight: 600;
                  `}
                >
                  产品规格重复
                </div>
              );
            }}
          >
            <Table.Column
              title="#"
              render={(_row, _record, rowIdx) => {
                return <>{rowIdx + 1}</>;
              }}
            />
            <Table.Column
              title="封面"
              render={(_row, _record, rowIdx) => {
                return (
                  <Form.Item noStyle name={[rowIdx, 'cover']} rules={[{ required: true }]}>
                    <HAssetSelect width={48} height={48} />
                  </Form.Item>
                );
              }}
            />

            {/* ============= 规格属性 ============= */}
            {variantAttributeList.list.map((o, attributeIdx) => {
              return (
                <Table.Column
                  key={attributeIdx}
                  title={
                    <Space>
                      <HMartAttributeMeta attributeId={o}>{(data) => data.name}</HMartAttributeMeta>
                      <HCloseButton
                        onClick={() => {
                          variantAttributeList.remove(attributeIdx);

                          variants.forEach((variant) => {
                            const names = ['variants', variant.name, 'attributes'];
                            const v: any[] = form.getFieldValue(names) || [];
                            const newAttributes = v.filter((_value, i) => i !== attributeIdx);
                            form.setFieldValue(names, newAttributes);
                          });
                        }}
                      />
                    </Space>
                  }
                  render={(_value, _record, rowIdx) => {
                    return (
                      <AttributeFormItem
                        onLoaded={(data) => {
                          form.setFieldValue(['variants', rowIdx, 'attributes', attributeIdx, 'attributeId'], data.id);
                          form.setFieldValue(
                            ['variants', rowIdx, 'attributes', attributeIdx, 'valueType'],
                            data.valueType
                          );
                        }}
                        noStyle
                        name={[rowIdx, 'attributes', attributeIdx]}
                        attributeId={o}
                        validateStatus={variantErrors.has(rowIdx) ? 'error' : ''}
                        rules={[{ required: true }]}
                        normalize={(value) => value.trim()}
                      />
                    );
                  }}
                />
              );
            })}
            {/* ============= 规格属性 ============= */}
            <Table.Column
              title="价格"
              render={(_value, _record, rowIdx) => {
                return (
                  <ProFormDigit
                    noStyle
                    name={[rowIdx, 'price']}
                    rules={[{ required: true }, { type: 'number' }]}
                    placeholder={'价格'}
                  />
                );
              }}
            />
            <Table.Column
              title="长"
              render={(_value, _record, rowIdx) => {
                return (
                  <ProFormDigit
                    noStyle
                    name={[rowIdx, 'length']}
                    rules={[{ type: 'number', min: 1 }]}
                    placeholder={'长'}
                  />
                );
              }}
            />
            <Table.Column
              title="宽"
              render={(_value, _record, rowIdx) => {
                return (
                  <ProFormDigit
                    noStyle
                    name={[rowIdx, 'width']}
                    rules={[{ type: 'number', min: 1 }]}
                    placeholder={'宽'}
                  />
                );
              }}
            />
            <Table.Column
              title="高"
              render={(_value, _record, rowIdx) => {
                return (
                  <ProFormDigit
                    noStyle
                    name={[rowIdx, 'height']}
                    rules={[{ type: 'number', min: 1 }]}
                    placeholder={'高'}
                  />
                );
              }}
            />
            <Table.Column
              title="重量"
              render={(_value, _record, rowIdx) => {
                return (
                  <ProFormDigit
                    noStyle
                    name={[rowIdx, 'weight']}
                    rules={[{ type: 'number', min: 1 }]}
                    placeholder={'重量'}
                  />
                );
              }}
            />
            <Table.Column
              align="right"
              render={(_value, _record, rowIdx) => {
                return (
                  <HCloseButton
                    onClick={() => {
                      remove(rowIdx);
                    }}
                  />
                );
              }}
            />
          </Table>
        </Card>
      )}
    </Form.List>
  );

  // 生成限定名称
  const submit = async (values: any) => {
    console.log('new product', values);

    MART_API.addMartProduct({ upsertMartProductRequest: values });
    message.success('新增成功');
    // form.resetFields();
  };

  // 检查产品规格
  const preCheck = (values: any) => {
    const variants = values.variants || [];

    // 限定名
    const qns: string[] = [];
    variants.forEach((variant: any, i: number) => {
      const qn = (variant.attributes || []).map((o: any) => o.value ?? o.attributeValueId).join('|');
      if (qns.indexOf(qn) === -1) {
        qns.push(qn);
        variantErrorOpts.remove(i);
      } else {
        variantErrorOpts.add(i);
      }
    });
    return qns.length === variants.length;
  };

  return (
    <PageContainer title={'新建产品'}>
      <ProForm
        form={form}
        submitter={{
          render: (_, dom) => <FooterToolbar>{dom}</FooterToolbar>,
        }}
        onFinish={async (values) => {
          if (!preCheck(values)) {
            return false;
          }
          await submit(values);
        }}
        className={css`
          display: flex;
          flex-direction: column;
          row-gap: var(--ant-margin);
        `}
      >
        {basicPanel}

        {attributePanel}

        {variantPanel}
      </ProForm>
    </PageContainer>
  );
};
