import { HSearchButton } from '@/components/HSearchButton';
import { PageContainer } from '@/components/PageContainer';
import { useRSQLFilter, useTableHelper } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { Form, Input, Table, TableColumnProps } from '@arco-design/web-react';
import { useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';

const bytesToSize = (bytes: number) => {
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
  if (bytes === 0) return '0';
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  if (i === 0) return `${bytes} ${sizes[i]}`;
  return `${(bytes / 1024 ** i).toFixed(1)} ${sizes[i]}`;
};

export const AccessLogList = () => {
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ field: 'update_time', direction: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();

  const { data, isLoading } = useQuery({
    queryKey: ['/system/access-logs', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listAccessLogs({
        ...queryOffsetLimit,
        sort: querySort,
        filter: queryFilter,
      });
    },
  });

  const columns: TableColumnProps[] = [
    {
      title: 'ID',
      dataIndex: 'id',
    },
    {
      key: 'create_time',
      title: '访问时间',
      sorter: true,
      defaultSortOrder: 'descend',
      render(col, item, index) {
        return dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss');
      },
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
      render(col, item, index) {
        return bytesToSize(item.responseBytes ?? 0);
      },
    },
  ];

  return (
    <PageContainer title={'访问日志'}>
      <Form
        layout="inline"
        onSubmit={(values) => {
          gotoFirstPage();
          setRSQLFilters([
            rsqlOps.comparisonEx('employee_id', '==', values.employeeId),
            rsqlOps.comparisonEx('ip_addr', '==', values.ipAddr),
          ]);
        }}
      >
        <Form.Item field={'employeeId'} label="职员 ID">
          <Input allowClear />
        </Form.Item>
        <Form.Item field={'ipAddr'} label="IP">
          <Input allowClear />
        </Form.Item>
        <HSearchButton loading={isLoading} />
      </Form>

      <Table
        columns={columns}
        onChange={onTableChange}
        loading={isLoading}
        data={data?.items}
        pagination={{
          ...pagination,
          total: data?.totalCount,
        }}
      />
    </PageContainer>
  );
};
