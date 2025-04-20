import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { HEditButton } from '@/components/HEditButton';
import { HIncludeDetedCheckBox } from '@/components/HIncludeDeletedCheckbox';
import { HNewButton } from '@/components/HNewButton';
import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { MART_API, resolveApiError } from '@/services';
import { PlusCircleOutlined } from '@ant-design/icons';
import {
  DrawerForm,
  ModalForm,
  PageContainer,
  ProFormCheckbox,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { css } from '@styled-system/css';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useCounter } from 'ahooks';
import { Button, Form, Input, message, Space, Table } from 'antd';
import { FormInstance } from 'antd/lib';
import { useRef, useState } from 'react';

export const MartAttributeList = () => {
  const noWrite = $checkNotPermit(permits.MART_ATTRIBUTE.W);

  const [incldeDeleted, setIncludeDeleted] = useState<number | undefined>();
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'ordering', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['MarketAttributeList', queryOffsetLimit, queryFilter, querySort, incldeDeleted],
    async queryFn() {
      return MART_API.listMartAttributes({
        ...queryOffsetLimit,
        sort: querySort,
        filter: queryFilter,
        includeDeleted: incldeDeleted,
      });
    },
  });

  const searchForm = (
    <Form
      layout="inline"
      onFinish={(values: any) => {
        gotoFirstPage();
        setRSQLFilters([
          rsqlOps.comparisonEx('slug', '==', values.searchSlug),
          rsqlOps.comparisonEx('name', '=icontains=', values.searchName),
        ]);
      }}
    >
      <ProFormText name="searchName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const CommonFileds = () => {
    return (
      <>
        <ProFormText
          label="名称"
          name="name"
          required
          validateTrigger="onSubmit"
          rules={[{ required: true }, { max: 32, message: '最长32个字符' }]}
        />
        <ProFormTextArea label="备注" name="remark" rules={[{ type: 'string', max: 256 }]} />
        <ProFormDigit label="排序" name={'ordering'} min={0} max={99999} rules={[{ required: true }]} />
        <ProFormSelect
          label="值类型"
          name={'valueType'}
          options={[
            { label: '输入', value: 'INPUT' },
            { label: '选择', value: 'SELECT' },
          ]}
          rules={[{ required: true }]}
        />
        <div>
          <Space>
            <ProFormCheckbox name={'searchable'}>可搜索</ProFormCheckbox>
            <ProFormCheckbox name={'required'}>必埴项</ProFormCheckbox>
          </Space>
        </div>
      </>
    );
  };

  const NewForm = () => {
    const { mutateAsync } = useMutation<any>({
      mutationKey: ['MarketAttributeList.NewForm'],
      mutationFn(values: any) {
        return MART_API.createMartAttribute({
          upsertMartAttributeRequest: values,
        });
      },
      onSuccess() {
        message.success('新增成功');
        refetch();
      },
      async onError(error) {
        const err = await resolveApiError(error);
        message.error(err.message);
      },
    });

    return (
      <DrawerForm
        title="新增商品属性"
        trigger={<HNewButton disabled={noWrite} />}
        onFinish={async (values: any) => {
          await mutateAsync(values);
          return true;
        }}
      >
        <CommonFileds />
      </DrawerForm>
    );
  };

  const EditForm = ({ id }: { id: number }) => {
    const { mutateAsync } = useMutation<any>({
      mutationKey: ['MarketAttributeList.EditForm'],
      mutationFn(values: any) {
        return MART_API.updateMartAttribute({
          id,
          upsertMartAttributeRequest: {
            ...values,
          },
        });
      },
      onSuccess() {
        message.success('修改成功');
        refetch();
      },
      async onError(error) {
        const err = await resolveApiError(error);
        message.error(err.message);
      },
    });

    return (
      <DrawerForm
        title="修改商品属性"
        trigger={<HEditButton disabled={noWrite} />}
        onInit={async (_, form) => {
          const values = await MART_API.getMartAttribute({ id });
          form.setFieldsValue(values);
        }}
        onFinish={async (values: any) => {
          await mutateAsync(values);
          return false;
        }}
      >
        <Form.Item label="ID">
          <Input disabled value={id} />
        </Form.Item>
        <CommonFileds />
      </DrawerForm>
    );
  };

  const onDelete = async (id: number) => {
    await MART_API.deleteMartAttribute({ id });
    message.success('删除成功');
    refetch();
  };

  // 商品属性
  const NewValueForm = ({ attributeId }: { attributeId: number }) => {
    const [uc, ucOps] = useCounter(0);
    const formRef = useRef<FormInstance>();
    const submit = async (values: any) => {
      try {
        await MART_API.createMartAttributeValue({
          attributeId,
          upsertMartAttributeValueRequest: {
            ...values,
          },
        });
        message.success('新增成功');
      } catch (error) {
        const err = await resolveApiError(error);
        message.error(err.message);
        throw error;
      }
    };

    return (
      <ModalForm
        title={'新增属性值'}
        trigger={<Button color="default" variant="link" icon={<PlusCircleOutlined />} disabled={noWrite} />}
        formRef={formRef}
        modalProps={{
          afterClose() {
            if (uc > 0) {
              refetch();
            }
            ucOps.reset();
          },
        }}
        initialValues={{ ordering: 0 }}
        onFinish={async (values) => {
          await submit(values);

          ucOps.inc();
          formRef.current?.resetFields();
          return false;
        }}
      >
        <ProFormText label={'值'} name={'value'} rules={[{ required: true }, { type: 'string', min: 1, max: 32 }]} />
        <ProFormDigit label="排序" name={'ordering'} min={0} max={99999} rules={[{ required: true }]} />
      </ModalForm>
    );
  };

  // 修改属性值
  const EditValueForm = ({ attributeId, attributeValueId }: { attributeId: number; attributeValueId: number }) => {
    const submit = async (values: any) => {
      try {
        await MART_API.updateMartAttributeValue({
          attributeId,
          attributeValueId,
          upsertMartAttributeValueRequest: {
            ...values,
          },
        });
        message.success('修改成功');
      } catch (error) {
        const err = await resolveApiError(error);
        message.error(err.message);
        throw error;
      }
    };

    return (
      <ModalForm
        title={'修改属性值'}
        trigger={<HEditButton disabled={noWrite} />}
        modalProps={{
          afterClose() {
            refetch();
          },
        }}
        onFinish={async (values) => {
          await submit(values);
          return true;
        }}
        onInit={async (_values, form) => {
          const values = await MART_API.getMartAttributeValue({ attributeId, attributeValueId });
          form.setFieldsValue({
            ...values,
          });
        }}
      >
        <Form.Item label="ID">
          <Input disabled value={attributeValueId} />
        </Form.Item>
        <ProFormText label={'值'} name={'value'} rules={[{ required: true }, { type: 'string', min: 1, max: 32 }]} />
        <ProFormDigit label="排序" name={'ordering'} min={0} max={99999} rules={[{ required: true }]} />
      </ModalForm>
    );
  };

  const onDeleteValue = async ({
    attributeId,
    attributeValueId,
  }: {
    attributeId: number;
    attributeValueId: number;
  }) => {
    await MART_API.deleteMartAttributeValue({ attributeId, attributeValueId });
    message.success('删除成功');
    refetch();
  };

  return (
    <PageContainer>
      <ProTable
        search={false}
        manualRequest
        options={{
          reload() {
            refetch();
          },
        }}
        toolbar={{
          search: searchForm,
          actions: [<NewForm />],
          filter: <HIncludeDetedCheckBox onChange={(e) => setIncludeDeleted(e.target.checked ? 1 : undefined)} />,
        }}
        loading={isFetching}
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'atable.state.MarketAttributeList',
        }}
        rowKey={'id'}
        columns={[
          {
            title: 'ID',
            dataIndex: 'id',
          },
          {
            title: '名称',
            dataIndex: 'name',
          },
          {
            title: '备注',
            dataIndex: 'remark',
            ellipsis: true,
          },
          {
            title: '排序',
            dataIndex: 'ordering',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            title: '值类型',
            dataIndex: 'valueType',
            valueEnum: {
              INPUT: {
                text: '输入',
                status: 'default',
              },
              SELECT: {
                text: '选择',
                status: 'success',
              },
            },
          },
          {
            title: '可搜索',
            render(_dom, row) {
              return row.searchable ? '️✓' : '✗';
            },
          },
          {
            title: '必填项',
            render(_dom, row) {
              return row.required ? '✓' : '✗';
            },
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render(_dom, row) {
              if (row.deleted) {
                return <></>;
              }
              return (
                <>
                  <EditForm id={row.id!} />
                  <HDeletePopconfirmButton onConfirm={() => onDelete(row.id!)} disabled={noWrite} />
                </>
              );
            },
          },
        ]}
        expandable={{
          rowExpandable: (record) => record.valueType === 'SELECT',
          expandedRowRender(row) {
            const dataSource = row.attributeValues ?? [];
            return (
              <Table
                title={() => {
                  return (
                    <div
                      className={css`
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                      `}
                    >
                      <span>可选值</span>
                      {!row.deleted && <NewValueForm attributeId={row.id!} />}
                    </div>
                  );
                }}
                pagination={false}
                size="small"
                columns={[
                  {
                    title: '值',
                    dataIndex: 'value',
                  },
                  {
                    title: '排序',
                    dataIndex: 'ordering',
                  },
                  {
                    title: '操作',
                    align: 'right',
                    render(_dom, subRow) {
                      if (row.deleted || subRow.deleted) {
                        return <></>;
                      }
                      return (
                        <>
                          {/* <EditValueForm attributeId={row.id!} attributeValueId={subRow.id!} /> */}
                          <HDeletePopconfirmButton
                            onConfirm={() => onDeleteValue({ attributeId: row.id!, attributeValueId: subRow.id! })}
                            disabled={noWrite}
                          />
                        </>
                      );
                    },
                  },
                ]}
                dataSource={dataSource}
              />
            );
          },
        }}
      ></ProTable>
    </PageContainer>
  );
};
