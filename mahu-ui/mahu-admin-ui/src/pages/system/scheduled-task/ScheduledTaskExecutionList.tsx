import { SYSTEM_API } from '@/services';
import { ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { useTableHelper } from '@/hooks/useTableHelper';

export const ScheduledTaskExecutionList = ({ taskName, taskInstance }: { taskName: string; taskInstance: string }) => {
  const { onTableChange, pagination, queryOffsetLimit, querySort } = useTableHelper({});
  const { data, isFetching } = useQuery({
    queryKey: ['listScheduledTaskExecutions', queryOffsetLimit, querySort],
    async queryFn() {
      return SYSTEM_API.listScheduledTaskExecutions({
        taskName,
        taskInstance,
        ...queryOffsetLimit,
        sort: querySort,
      });
    },
  });

  return (
    <ProTable
      headerTitle={false}
      search={false}
      options={false}
      showSorterTooltip={false}
      manualRequest
      loading={isFetching}
      dataSource={data?.items}
      pagination={{ ...pagination, total: data?.totalCount, showSizeChanger: false }}
      onChange={onTableChange}
      rowKey={(record, idx) => `${idx}`}
      columns={[
        {
          title: '名称',
          dataIndex: 'taskName',
        },
        {
          title: '实例',
          dataIndex: 'taskInstance',
        },
        {
          title: '执行器',
          dataIndex: 'pickedBy',
        },
        {
          title: '开始时间',
          dataIndex: 'startedAt',
          valueType: 'dateTime',
        },
        {
          title: '完成时间',
          dataIndex: 'finishedAt',
          valueType: 'dateTime',
        },
        {
          title: '状态',
          dataIndex: 'succeeded',
          renderText(text, record, index, action) {
            return record.succeeded ? 'T' : 'F';
          },
          valueEnum: {
            T: {
              text: '成功',
              status: 'success',
            },
            F: {
              text: '失败',
              status: 'error',
            },
          },
        },
        {
          title: '错误原因',
          dataIndex: 'cause',
          width: 30,
          renderText(text, record, index, action) {
            return JSON.stringify(record.cause);
          },
        },
      ]}
    ></ProTable>
  );
};
