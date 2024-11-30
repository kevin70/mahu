import { HSearchButton } from '@/components/HSearchButton';
import { useDataFilter, usePagination, useTableSorter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form } from 'antd';

export const AccessLogList = () => {
  const { pagination, setPagination, setTotal, queryOffsetLimit } = usePagination();
  const { setDataFilters, queryFilter } = useDataFilter();
  const { setTableSorter, querySort } = useTableSorter([{ columnKey: 'create_time' }]);
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/access-logs', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      const res = await SYSTEM_API.listAccessLogs(
        queryOffsetLimit.limit,
        queryOffsetLimit.offset,
        queryFilter,
        querySort
      );
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
            qname: 'employee_id',
            op: 'eq',
            value: values.employeeId,
          },
          {
            qname: 'ip_addr',
            op: 'eq',
            value: values.ipAddr,
          },
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
            key: 'create_time',
            title: '访问时间',
            dataIndex: 'createTime',
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
