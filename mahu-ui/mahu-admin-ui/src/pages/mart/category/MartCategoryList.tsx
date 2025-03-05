import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { HEditButton } from '@/components/HEditButton';
import { HIncludeDetedCheckBox } from '@/components/HIncludeDeletedCheckbox';
import { HNewButton } from '@/components/HNewButton';
import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { MART_API, resolveApiError } from '@/services';
import {
  DrawerForm,
  PageContainer,
  ProFormCheckbox,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { useMutation, useQuery } from '@tanstack/react-query';
import { Form, Input, message, Space } from 'antd';
import { useState } from 'react';

export const MartCategoryList = () => {
  const noWrite = $checkNotPermit(permits.MART_CATEGORY.W);

  const [incldeDeleted, setIncludeDeleted] = useState<number | undefined>();
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'ordering', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['MarketCategoryList', queryOffsetLimit, queryFilter, querySort, incldeDeleted],
    async queryFn() {
      return MART_API.listMartCategories({
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
        <ProFormText label="名称" name="name" rules={[{ required: true }, { max: 32, message: '最长32个字符' }]} />
        <ProFormTextArea label="备注" name="remark" rules={[{ type: 'string', max: 256 }]} />
        <ProFormDigit label="排序" name={'ordering'} min={0} max={99999} rules={[{ required: true }]} />
      </>
    );
  };

  // 新增
  const NewForm = () => {
    const { mutateAsync } = useMutation<any>({
      mutationKey: ['MarketCategoryList.NewForm'],
      mutationFn(values: any) {
        return MART_API.addMartCategory({
          upsertMartCategoryRequest: values,
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
        title="新增产品分类"
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

  // 编辑
  const EditForm = ({ id }: { id: number }) => {
    const { mutateAsync } = useMutation<any>({
      mutationKey: ['MarketCategoryList.EditForm'],
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
          persistenceKey: 'atable.state.MarketCategoryList',
        }}
        rowKey={'id'}
        columns={[
          {
            title: 'ID',
            dataIndex: 'id',
          },
          {
            key: 'created_at',
            title: '创建时间',
            dataIndex: 'createdAt',
            valueType: 'dateTime',
            sorter: true,
          },
          {
            key: 'updated_at',
            title: '更新时间',
            dataIndex: 'updatedAt',
            valueType: 'dateTime',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            title: '名称',
            dataIndex: 'name',
          },
          {
            title: '父分类',
            dataIndex: 'parent.name',
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
      ></ProTable>
    </PageContainer>
  );
};
