import { usePagination, useRSQLFilter, useTableSorter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, Typography } from 'antd';
import { NewDictDrawerForm } from './NewDictDrawerForm';
import { HSearchButton } from '@/components/HSearchButton';
import { EditDictDrawerForm } from './EditDictDrawerForm';
import { permits } from '@/config/permit';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';

export const DictList = () => {
  const noWrite = $checkNotPermit(permits.DICT.W);
  const { pagination, setPagination, resetPagination, setTotal, queryOffsetLimit } = usePagination();
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { setTableSorter, querySort } = useTableSorter([{ columnKey: 'ordering' }]);
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/dicts', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      const res = await SYSTEM_API.listDicts({
        ...queryOffsetLimit,
        sort: querySort,
        filter: queryFilter,
      });
      setTotal(res.totalCount);
      return res.items;
    },
  });

  const searchForm = (
    <Form
      layout="inline"
      onFinish={(values: any) => {
        resetPagination();
        setRSQLFilters([
          rsqlOps.comparisonEx('slug', '==', values.searchSlug),
          rsqlOps.comparisonEx('kind', '=contains=', values.searchKind),
        ]);
      }}
    >
      <ProFormText name="searchKind" label="种类" />
      <ProFormText name="searchSlug" label="SLUG" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await SYSTEM_API.deleteDict({ id });
    $message().success('字典删除成功');

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
          actions: [<NewDictDrawerForm onSuccess={refetch} />],
        }}
        loading={isFetching}
        dataSource={data}
        pagination={pagination}
        onChange={(pagination, _filters, sorter, _extra) => {
          setPagination(pagination);
          setTableSorter(sorter);
        }}
        columns={[
          {
            title: 'ID',
            dataIndex: 'id',
          },
          {
            title: 'SLUG',
            dataIndex: 'slug',
          },
          {
            title: '种类',
            dataIndex: 'kind',
          },
          {
            title: '值',
            dataIndex: 'value',
            ellipsis: true,
          },
          {
            title: '文本',
            dataIndex: 'label',
            ellipsis: true,
          },
          {
            title: '备注',
            dataIndex: 'remark',
            ellipsis: true,
          },
          {
            title: '排序',
            dataIndex: 'ordering',
            key: 'ordering',
            sorter: true,
            defaultSortOrder: 'descend',
            showSorterTooltip: false,
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => [
              <EditDictDrawerForm id={row.id!} onSuccess={refetch} />,
              <HDeletePopconfirmButton
                onConfirm={() => onDelete(row.id!)}
                description={() => (
                  <div>
                    确认删除字典 <Typography.Text mark>{row.kind}</Typography.Text>{' '}
                    <Typography.Text mark>{row.label}</Typography.Text>
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
