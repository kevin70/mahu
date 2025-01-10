import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { BASIS_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, message } from 'antd';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { NewBrandDrawerForm } from './NewBrandDrawerForm';
import { EditBrandDrawerForm } from './EditBrandDrawerForm';
import { useTableHelper } from '@/hooks/useTableHelper';

export const BrandList = () => {
  const noWrite = $checkNotPermit(permits.BRAND.W);
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'update_time' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/brands', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return BASIS_API.listBrands({
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
        setRSQLFilters([rsqlOps.comparisonEx('name', '=icontains=', values.searchName)]);
      }}
    >
      <ProFormText name="searchName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await BASIS_API.deleteBrand({ id });
    message.success('删除品牌成功');

    await refetch();
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
          actions: [<NewBrandDrawerForm onSuccess={refetch} />],
        }}
        loading={isFetching}
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'atable_state_brand_list',
        }}
        columns={[
          {
            title: 'ID',
            dataIndex: 'id',
          },
          {
            key: 'create_time',
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
            sorter: true,
          },
          {
            key: 'update_time',
            title: '更新时间',
            dataIndex: 'updateTime',
            valueType: 'dateTime',
            sorter: true,
          },
          {
            title: '字母',
            dataIndex: 'firstLetter',
          },
          {
            title: '名称',
            dataIndex: 'name',
          },
          {
            title: 'LOGO',
            dataIndex: 'logo',
            valueType: 'image',
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
            render: (_dom, row) => [
              <EditBrandDrawerForm id={row.id} onSuccess={refetch} />,
              <HDeletePopconfirmButton onConfirm={() => onDelete(row.id)} disabled={noWrite} />,
            ],
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
