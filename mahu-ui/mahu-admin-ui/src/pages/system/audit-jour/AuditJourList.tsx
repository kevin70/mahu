import { HSearchButton } from '@/components/HSearchButton';
import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form } from 'antd';

export const AuditJourList = () => {
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'create_time', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/audit-jours', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listAuditJours({
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
          rsqlOps.comparisonEx('user_id', '==', values.userId),
          rsqlOps.comparisonEx('ip_addr', '==', values.ipAddr),
        ]);
      }}
    >
      <ProFormText name="userId" label="用户 ID" placeholder="用户 ID" />
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
            key: 'create_time',
            title: '操作时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            title: '操作 IP',
            dataIndex: 'ipAddr',
          },
          {
            title: '操作用户',
            dataIndex: 'userId',
          },
          {
            title: '来源',
            dataIndex: 'source',
          },
          {
            title: '类型',
            dataIndex: 'changeType',
            valueEnum: {
              D: {
                text: '删除',
                status: 'error',
              },
              I: {
                text: '新增',
                status: 'success',
              },
              U: {
                text: '修改',
                status: 'default',
              },
            },
          },
          {
            title: '表名',
            dataIndex: 'tableName',
          },
          {
            title: '数据 ID',
            dataIndex: 'dataId',
            ellipsis: true,
          },
          {
            title: '修改数据',
            dataIndex: 'data',
            ellipsis: true,
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
