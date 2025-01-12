import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { HEditButton } from '@/components/HEditButton';
import { HNewButton } from '@/components/HNewButton';
import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { MARKET_API, resolveApiError } from '@/services';
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';
import {
  DrawerForm,
  PageContainer,
  ProFormCheckbox,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { css } from '@emotion/react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { Button, Checkbox, Form, Input, message, Space } from 'antd';
import { useForm, useWatch } from 'antd/es/form/Form';
import { useMemo } from 'react';

export const MarketAttributeList = () => {
  const noWrite = $checkNotPermit(permits.MARKET_ATTRIBUTE.W);
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'update_time' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['MarketAttributeList', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return MARKET_API.listMarketAttributes({
        ...queryOffsetLimit,
        sort: querySort,
        filter: queryFilter,
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
    const valueType = useWatch('valueType');
    const needAttrValue = useMemo(() => valueType === 'SELECT', [valueType]);

    return (
      <>
        <ProFormText
          label="名称"
          name="name"
          required
          validateTrigger="onSubmit"
          rules={[{ required: true }, { max: 32, message: '最长32个字符' }]}
        />
        <ProFormSelect
          label="值类型"
          name={'valueType'}
          options={[
            { label: '输入', value: 'INPUT' },
            { label: '选择', value: 'SELECT' },
          ]}
          rules={[{ required: true }]}
        />
        <ProFormTextArea label="备注" name="remark" rules={[{ type: 'string', max: 256 }]} />
        <div>
          <Space>
            <ProFormCheckbox name={'searchable'}>可搜索</ProFormCheckbox>
            <ProFormCheckbox name={'required'}>必埴项</ProFormCheckbox>
          </Space>
        </div>

        {needAttrValue && (
          <Form.List name={'attributeValues'}>
            {(fields, { add, remove }) => {
              return (
                <>
                  {fields.map(({ key, name, ...otherProps }) => (
                    <Form.Item label="属性值" rules={[{ required: true }]}>
                      <Space key={key}>
                        <Form.Item {...otherProps} name={[name, 'value']} noStyle rules={[{ required: true }]}>
                          <Input placeholder="First Name" />
                        </Form.Item>
                        <Form.Item {...otherProps} name={[name, 'ordering']} noStyle rules={[{ required: true }]}>
                          <Input placeholder="Last Name" />
                        </Form.Item>
                        <MinusCircleOutlined onClick={() => remove(name)} />
                      </Space>
                    </Form.Item>
                  ))}

                  <Form.Item>
                    <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                      新增属性值
                    </Button>
                  </Form.Item>
                </>
              );
            }}
          </Form.List>
        )}
      </>
    );
  };

  const NewForm = () => {
    const { mutateAsync } = useMutation<any>({
      mutationKey: ['MarketAttributeList.NewForm'],
      mutationFn(values: any) {
        return MARKET_API.addMarketAttribute(values);
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

  const EditForm = ({ id }: { id?: number }) => {
    const { mutateAsync } = useMutation<any>({
      mutationKey: ['MarketAttributeList.EditForm'],
      mutationFn(values: any) {
        return MARKET_API.addMarketAttribute(values);
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
        title="修改商品属性"
        trigger={<HEditButton disabled={noWrite} />}
        onFinish={async (values: any) => {
          await mutateAsync(values);
          return true;
        }}
      >
        <Form.Item label="ID">
          <Input disabled value={id} />
        </Form.Item>
        <CommonFileds />
      </DrawerForm>
    );
  };

  const onDelete = (id?: number) => {
    //
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
        }}
        loading={isFetching}
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'atable.state.MarketAttributeList',
        }}
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
            title: '备注',
            dataIndex: 'remark',
            ellipsis: true,
          },
          {
            title: '可搜索',
            render(_dom, row) {
              return <Checkbox checked={row.searchable} disabled />;
            },
          },
          {
            title: '必填项',
            render(_dom, row) {
              return <Checkbox checked={row.required} disabled />;
            },
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => [
              <EditForm id={row.id} />,
              <HDeletePopconfirmButton onConfirm={() => onDelete(row.id)} disabled={noWrite} />,
            ],
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
