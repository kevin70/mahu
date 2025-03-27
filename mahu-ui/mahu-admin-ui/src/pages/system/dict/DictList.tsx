import { useRSQLFilter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, message } from 'antd';
import { NewDictDrawerForm } from './NewDictDrawerForm';
import { HSearchButton } from '@/components/HSearchButton';
import { EditDictDrawerForm } from './EditDictDrawerForm';
import { permits } from '@/config/permit';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { useTableHelper } from '@/hooks/useTableHelper';

export const DictList = () => {
  const noWrite = $checkNotPermit(permits.DICT.W);
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'updated_at' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/dicts', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listSystemDicts({
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
          rsqlOps.comparisonEx('type_code', '==', values.searchSlug),
          rsqlOps.comparisonEx('kind', '=contains=', values.searchKind),
        ]);
      }}
    >
      <ProFormText name="searchKind" label="种类" />
      <ProFormText name="searchSlug" label="SLUG" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

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
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'atable.state.system.DictList',
        }}
        rowKey={'typeCode'}
        columns={[
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
          },
          {
            title: 'CODE',
            dataIndex: 'typeCode',
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
                  <EditDictDrawerForm id={row.id!} onSuccess={refetch} />
                  <HDeletePopconfirmButton onConfirm={() => onDelete(row.id!)} disabled={noWrite} />
                </>
              );
            },
          },
        ]}
        expandable={{
          rowExpandable(row) {
            return (row.data?.length || 0) > 0;
          },
          expandedRowRender(row) {
            const dataSource = row.data ?? [];
            return (
              <ProTable
                search={false}
                options={false}
                pagination={false}
                size="small"
                columns={[
                  {
                    title: 'CODE',
                    dataIndex: 'dataCode',
                  },
                  {
                    title: '名称',
                    dataIndex: 'name',
                  },
                  {
                    title: '值',
                    dataIndex: 'value',
                  },
                  {
                    title: '排序值',
                    dataIndex: 'ordering',
                  },
                  {
                    title: '状态',
                    dataIndex: 'status',
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
