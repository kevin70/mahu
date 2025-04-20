import { useRSQLFilter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Button, Form, message, Typography } from 'antd';
import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useTableHelper } from '@/hooks/useTableHelper';
import { css } from '@styled-system/css';
import { VscPlayCircle } from 'react-icons/vsc';
import { ScheduledTaskExecutionList } from './ScheduledTaskExecutionList';

export const ScheduledTaskList = () => {
  const noWrite = $checkNotPermit(permits.SCHEDULED_TASK.W);

  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({});
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['listScheduledTasks', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listScheduledTasks({
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
        setRSQLFilters([rsqlOps.comparisonEx('task_name', '=icontains=', values.taskName)]);
      }}
      className={css`
        & > .ant-form-item {
          min-width: 240px;
        }
      `}
    >
      <ProFormText name="taskName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await SYSTEM_API.deleteEmployee({ id });
    message.success('删除职员成功');

    await refetch();
  };

  return (
    <PageContainer>
      <ProTable
        search={false}
        showSorterTooltip={false}
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
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'ant.table.listScheduledTasks',
        }}
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
            title: '数据',
            dataIndex: 'taskData',
            ellipsis: true,
          },
          {
            title: '版本',
            dataIndex: 'version',
          },
          {
            title: '优先级',
            dataIndex: 'priority',
          },
          {
            title: '执行时间',
            dataIndex: 'executionTime',
            valueType: 'dateTime',
            tooltip: '任务下次的执行时间',
          },
          {
            title: '执行者',
            dataIndex: 'pickedBy',
            tooltip: '任务正在被哪台实例执行',
          },
          {
            title: '上次成功时间',
            dataIndex: 'lastSuccess',
            valueType: 'dateTime',
            render(dom, row) {
              if (row.lastSuccess) {
                return <Typography.Text type="success">{dom}</Typography.Text>;
              }
              return dom;
            },
          },
          {
            title: '连续失败次数',
            dataIndex: 'consecutiveFailures',
            valueType: 'dateTime',
            render(dom, row) {
              if (row.consecutiveFailures) {
                return <Typography.Text type="danger">{dom}</Typography.Text>;
              }
              return dom;
            },
          },
          {
            key: 'last_failure',
            title: '上次失败时间',
            dataIndex: 'lastFailure',
            valueType: 'dateTime',
            sorter: true,
            render(dom, row) {
              if (row.lastFailure) {
                return <Typography.Text type="danger">{dom}</Typography.Text>;
              }
              return dom;
            },
          },
          {
            title: '健康检测时间',
            dataIndex: 'lastHeartbeat',
            valueType: 'dateTime',
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => {
              return (
                <>
                  <Button color="default" variant="link" disabled={noWrite || row.picked} icon={<VscPlayCircle />} />
                </>
              );
            },
          },
        ]}
        expandable={{
          expandedRowRender(record) {
            return <ScheduledTaskExecutionList taskName={record.taskName} taskInstance={record.taskInstance} />;
          },
        }}
      ></ProTable>
    </PageContainer>
  );
};
