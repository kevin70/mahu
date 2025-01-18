import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { MART_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, message } from 'antd';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { NewMartShopForm } from './NewMartShopForm';
import { EditMartShopForm } from './EditMartShopForm';
import { useTableHelper } from '@/hooks/useTableHelper';

export const MartShopList = () => {
  const noWrite = $checkNotPermit(permits.MART_SHOP.W);
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'update_time' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['MarketShopList', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return MART_API.listShops({
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
      <ProFormText name="searchSlug" label="SLUG" />
      <ProFormText name="searchName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await MART_API.deleteShop({ id });
    message.success('删除商店成功');

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
          actions: [<NewMartShopForm onSuccess={refetch} />],
        }}
        loading={isFetching}
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'atable_state_market_shop_list',
        }}
        rowKey={'id'}
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
            defaultSortOrder: 'descend',
          },
          {
            title: 'SLUG',
            dataIndex: 'slug',
          },
          {
            title: '名称',
            dataIndex: 'name',
          },
          {
            title: '描述',
            dataIndex: 'description',
            ellipsis: true,
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => {
              return (
                <>
                  <EditMartShopForm id={row.id} onSuccess={refetch} />
                  <HDeletePopconfirmButton onConfirm={() => onDelete(row.id)} disabled={noWrite} />
                </>
              );
            },
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
