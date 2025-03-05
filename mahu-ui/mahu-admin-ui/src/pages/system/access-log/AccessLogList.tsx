import { HSearchButton } from '@/components/HSearchButton';
import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form } from 'antd';

export const AccessLogList = () => {
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'created_at', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/access-logs', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listAccessLogs({
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
          rsqlOps.comparisonEx('employee_id', '==', values.employeeId),
          rsqlOps.comparisonEx('ip_addr', '==', values.ipAddr),
        ]);
      }}
    >
      <ProFormText name="employeeId" label="职员 ID" placeholder="职员 ID" />
      <ProFormText name="ipAddr" label="IP" placeholder="访问 IP 地址" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const bytesToSize = (bytes: number) => {
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes === 0) return '0';
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    if (i === 0) return `${bytes} ${sizes[i]}`;
    return `${(bytes / 1024 ** i).toFixed(1)} ${sizes[i]}`;
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
            title: '职员 ID',
            dataIndex: 'employeeId',
          },
          {
            title: '方法',
            dataIndex: 'method',
          },
          {
            title: '路径',
            dataIndex: 'uriPath',
          },
          {
            title: '协议',
            dataIndex: 'protocol',
          },
          {
            title: 'Referer',
            dataIndex: 'referer',
            ellipsis: true,
          },
          {
            title: 'User Agent',
            dataIndex: 'userAgent',
            ellipsis: true,
          },
          {
            title: '响应码',
            dataIndex: 'responseStatus',
          },
          {
            title: '响应字节',
            dataIndex: 'responseBytes',
            renderText(text, record, index, action) {
              return bytesToSize(record.responseBytes ?? 0);
            },
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
