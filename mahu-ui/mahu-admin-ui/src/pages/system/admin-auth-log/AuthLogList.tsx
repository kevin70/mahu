import { HSearchButton } from '@/components/HSearchButton';
import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { LOG_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form } from 'antd';

export const AuthLogList = () => {
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'created_at', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['listAdminAuthLogs', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return LOG_API.listAdminAuthLogs({
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
        setRSQLFilters([
          rsqlOps.comparisonEx('admin_id', '==', values.adminId),
          rsqlOps.comparisonEx('ip_addr', '==', values.ipAddr),
        ]);
        gotoFirstPage();
      }}
    >
      <ProFormText name="adminId" label="管理员ID" placeholder="管理员ID" />
      <ProFormText name="ipAddr" label="IP" placeholder="访问 IP 地址" />
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
        }}
        loading={isFetching}
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        rowKey={'id'}
        columns={[
          {
            title: 'ID',
            dataIndex: 'id',
          },
          {
            key: 'created_at',
            title: '访问时间',
            dataIndex: 'createdAt',
            valueType: 'dateTime',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            title: '访问 IP',
            dataIndex: 'ipAddr',
          },
          {
            title: '管理员 ID',
            dataIndex: 'adminId',
          },
          {
            title: '终端 ID',
            dataIndex: 'clientId',
          },
          {
            title: '认证类型',
            dataIndex: 'authType',
          },
          {
            title: '认证方法',
            dataIndex: 'authMethod',
          },
          {
            title: 'User Agent',
            dataIndex: 'userAgent',
            ellipsis: true,
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
