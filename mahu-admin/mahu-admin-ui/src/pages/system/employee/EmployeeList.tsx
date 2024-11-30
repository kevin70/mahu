import { useDataFilter, usePagination, useTableSorter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, Typography } from 'antd';
import { NewEmployeeDrawerForm } from './NewEmployeeDrawerForm';
import { HSearchButton } from '@/components/HSearchButton';
import { EditEmployeeDrawerForm } from './EditEmployeeDrawerForm';
import { permits } from '@/config/permit';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { DepartmentTreeSelect } from '@/components/system/DepartmentTreeSelect';
import { css } from '@emotion/react';
import { EmployeeStatusSelect } from '@/components/system/EmployeeStatusSelect';
import { useState } from 'react';
import { HIncludeDetedCheckBox } from '@/components/HIncludeDeletedCheckbox';

export const EmployeeList = () => {
  const noWrite = $checkNotPermit(permits.DEPARTMENT.W);

  const [incldeDeleted, setIncludeDeleted] = useState<number | undefined>();
  const { pagination, setPagination, setTotal, queryOffsetLimit } = usePagination();
  const { setDataFilters, queryFilter } = useDataFilter();
  const { setTableSorter, querySort } = useTableSorter([{ columnKey: 'update_time' }]);
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/employees', queryOffsetLimit, queryFilter, querySort, incldeDeleted],
    async queryFn() {
      const res = await SYSTEM_API.listEmployees(
        queryOffsetLimit.limit,
        queryOffsetLimit.offset,
        queryFilter,
        querySort,
        incldeDeleted
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
            qname: 'nickname',
            op: 'contains',
            value: values.searchNickname,
          },
          {
            qname: 'status',
            op: 'in',
            value: values.searchStatus,
          },
          {
            qname: 'department.id',
            op: 'in',
            value: values.searchDepartmentIds,
          },
        ]);
      }}
    >
      <ProFormText name="searchNickname" label="昵称" />
      <Form.Item
        name="searchStatus"
        label="状态"
        css={css`
          width: 260px;
        `}
      >
        <EmployeeStatusSelect mode="multiple" placeholder="请选择" allowClear />
      </Form.Item>
      <Form.Item
        label="部门"
        name="searchDepartmentIds"
        css={css`
          width: 260px;
        `}
      >
        <DepartmentTreeSelect multiple placeholder="选择部门" allowClear treeDefaultExpandAll />
      </Form.Item>
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await SYSTEM_API.deleteEmployee(id);
    $message().success('删除职员成功');

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
          actions: [<NewEmployeeDrawerForm onSuccess={refetch} />],
          filter: <HIncludeDetedCheckBox onChange={(e) => setIncludeDeleted(e.target.checked ? 1 : undefined)} />,
        }}
        loading={isFetching}
        dataSource={data}
        pagination={pagination}
        onChange={(pagination, _filters, sorter, _extra) => {
          setPagination(pagination);
          setTableSorter(sorter);
        }}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'ant.table.system_employee_list',
        }}
        columns={[
          {
            title: 'ID',
            dataIndex: 'id',
          },
          {
            key: 'create_time',
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
            sorter: true,
          },
          {
            key: 'update_time',
            title: '更新时间',
            dataIndex: 'updateTime',
            valueType: 'dateTime',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            title: '用户名',
            dataIndex: 'username',
          },
          {
            title: '昵称',
            dataIndex: 'nickname',
          },
          {
            title: '头像',
            dataIndex: 'avatar',
            valueType: 'avatar',
          },
          {
            title: '状态',
            dataIndex: 'status',
            valueEnum: {
              ACTIVE: {
                text: '正常',
                status: 'success',
              },
              BLOCKED: {
                text: '冻结',
                status: 'error',
              },
              RESIGN: {
                text: '离职',
                status: 'default',
              },
            },
          },
          {
            title: '部门',
            dataIndex: ['department', 'name'],
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => [
              !row.deleted ? <EditEmployeeDrawerForm id={row.id} onSuccess={refetch} /> : null,
              !row.deleted ? (
                <HDeletePopconfirmButton
                  onConfirm={() => onDelete(row.id)}
                  description={() => (
                    <div>
                      确认删除职员 <Typography.Text mark>{row.username}</Typography.Text>{' '}
                      <Typography.Text mark>{row.nickname}</Typography.Text>
                    </div>
                  )}
                  disabled={noWrite || row.id === 1}
                />
              ) : null,
            ],
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
