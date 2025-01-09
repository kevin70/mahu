import { HSearchButton } from '@/components/HSearchButton';
import { PageContainer } from '@/components/PageContainer';
import { useRSQLFilter, useTableHelper } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { Badge, Form, Input, Table, TableColumnProps } from '@arco-design/web-react';
import { useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';

export const AuditJourList = () => {
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ field: 'update_time', direction: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();

  const { data, isLoading } = useQuery({
    queryKey: ['/system/access-logs', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listAuditJours({
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
      title: '操作时间',
      sorter: true,
      defaultSortOrder: 'descend',
      render(col, item, index) {
        return dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss');
      },
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
      render(col, item, index) {
        const { changeType } = item;
        if (changeType === 'D') {
          return <Badge dot status="error" text="删除" />;
        } else if (changeType === 'I') {
          return <Badge dot status="success" text="新增" />;
        }
        return <Badge dot status="default" text="修改" />;
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
  ];

  return (
    <PageContainer title={'操作审计'}>
      <Form
        layout="inline"
        onSubmit={(values) => {
          gotoFirstPage();
          setRSQLFilters([
            rsqlOps.comparisonEx('user_id', '==', values.userId),
            rsqlOps.comparisonEx('ip_addr', '==', values.ipAddr),
          ]);
        }}
      >
        <Form.Item field={'userId'} label="操作用户">
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
