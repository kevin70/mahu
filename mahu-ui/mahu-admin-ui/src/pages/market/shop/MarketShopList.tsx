import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useDataFilter, usePagination, useTableSorter } from '@/hooks';
import { BASIS_API, MARKET_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, Typography } from 'antd';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { NewMarketShopForm } from './NewMarketShopForm';
import { EditMarketShopForm } from './EditMarketShopForm';

export const MarketShopList = () => {
  const noWrite = $checkNotPermit(permits.MARKET_SHOP.W);
  const { pagination, setPagination, setTotal, queryOffsetLimit } = usePagination();
  const { setDataFilters, queryFilter } = useDataFilter();
  const { setTableSorter, querySort } = useTableSorter([{ columnKey: 'update_time' }]);
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['MarketShopList', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      const res = await MARKET_API.listShops(queryOffsetLimit.limit, queryOffsetLimit.offset, queryFilter, querySort);
      setTotal(res.totalCount);
      return res.items;
    },
  });

  const searchForm = (
    <Form
      layout="inline"
      onFinish={(values: any) => {
        setDataFilters([
          {
            qname: 'slug',
            op: 'eq',
            value: values.searchSlug,
          },
          {
            qname: 'name',
            op: 'icontains',
            value: values.searchName,
          },
        ]);
      }}
    >
      <ProFormText name="searchSlug" label="SLUG" />
      <ProFormText name="searchName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await MARKET_API.deleteShop(id);
    $message().success('删除商店成功');

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
          actions: [<NewMarketShopForm onSuccess={refetch} />],
        }}
        loading={isFetching}
        dataSource={data}
        pagination={pagination}
        onChange={(pagination, _filters, sorter, _extra) => {
          setPagination(pagination);
          setTableSorter(sorter);
        }}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'atable_state_market_shop_list',
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
            render: (_dom, row) => [
              <EditMarketShopForm id={row.id} onSuccess={refetch} />,
              <HDeletePopconfirmButton
                onConfirm={() => onDelete(row.id)}
                description={() => (
                  <div>
                    确认删除门店 <Typography.Text mark>{row.name}</Typography.Text>
                  </div>
                )}
                disabled={noWrite}
              />,
            ],
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
