import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { HIncludeDetedCheckBox } from '@/components/HIncludeDeletedCheckbox';
import { HNewButton } from '@/components/HNewButton';
import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { MART_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Button, Form, message } from 'antd';
import { useState } from 'react';
import { Link } from 'react-router';

export const MartProductList = () => {
  const noWrite = $checkNotPermit(permits.MART_PRODUCT.W);

  const [incldeDeleted, setIncludeDeleted] = useState<number | undefined>();
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'ordering', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['MartProductList', queryOffsetLimit, queryFilter, querySort, incldeDeleted],
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

  const onDelete = async (id: number) => {
    // await MART_API.deleteMartAttribute({ id });
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
          actions: [
            <Link to={'/mart/product-new'}>
              <HNewButton />
            </Link>,
          ],
          filter: <HIncludeDetedCheckBox onChange={(e) => setIncludeDeleted(e.target.checked ? 1 : undefined)} />,
        }}
        loading={isFetching}
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'atable.state.MartProductList',
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
                  <HDeletePopconfirmButton onConfirm={() => onDelete(row.id!)} disabled={noWrite} />
                </>
              );
            },
          },
        ]}
        expandable={{
          expandedRowRender(row) {
            return <></>;
          },
        }}
      ></ProTable>
    </PageContainer>
  );
};
