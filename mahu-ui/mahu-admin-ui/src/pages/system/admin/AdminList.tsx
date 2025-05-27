import { useRSQLFilter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, message } from 'antd';
import { NewAdminDrawerForm } from './NewAdminDrawerForm';
import { HSearchButton } from '@/components/HSearchButton';
import { EditAdminDrawerForm } from './EditAdminDrawerForm';
import { permits } from '@/config/permit';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { DepartmentTreeSelect } from '@/components/system/DepartmentTreeSelect';
import { AdminStatusSelect } from '@/components/system/AdminStatusSelect';
import { useState } from 'react';
import { HIncludeDetedCheckBox } from '@/components/HIncludeDeletedCheckbox';
import { useTableHelper } from '@/hooks/useTableHelper';
import { css } from '@styled-system/css';

export const AdminList = () => {
  const noWrite = $checkNotPermit(permits.ADMIN.W);

  const [incldeDeleted, setIncludeDeleted] = useState<number | undefined>();
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'updated_at' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/admins', queryOffsetLimit, queryFilter, querySort, incldeDeleted],
    async queryFn() {
      return SYSTEM_API.listAdmins({
        ...queryOffsetLimit,
        sort: querySort,
        filter: queryFilter,
        includeDeleted: incldeDeleted,
      });
    },
  });

  const searchForm = (
    <Form
      layout="inline"
      onFinish={(values: any) => {
        gotoFirstPage();
        setRSQLFilters([
          rsqlOps.comparisonEx('nickname', '=icontains=', values.searchNickname),
          rsqlOps.comparisonEx('status', '=in=', values.searchStatus),
          rsqlOps.comparisonEx('department_id', '=in=', values.searchDepartmentIds),
        ]);
      }}
      className={css`
        gap: var(--ant-margin-xs);
        & > .ant-form-item {
          min-width: 240px;
        }
      `}
    >
      <ProFormText name="searchNickname" label="昵称" />
      <Form.Item name="searchStatus" label="状态">
        <AdminStatusSelect mode="multiple" placeholder="请选择" allowClear />
      </Form.Item>
      <Form.Item label="部门" name="searchDepartmentIds">
        <DepartmentTreeSelect multiple placeholder="选择部门" allowClear treeDefaultExpandAll />
      </Form.Item>
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await SYSTEM_API.deleteAdmin({ id });
    message.success('删除管理员成功');

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
          actions: [<NewAdminDrawerForm onSuccess={refetch} />],
          filter: <HIncludeDetedCheckBox onChange={(e) => setIncludeDeleted(e.target.checked ? 1 : undefined)} />,
        }}
        loading={isFetching}
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'ant.table.system_employee_list',
        }}
        rowKey={'id'}
        columns={[
          {
            title: 'ID',
            dataIndex: 'id',
          },
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
            render: (_dom, row) => {
              if (row.deleted || row.id === 1) {
                return <></>;
              }
              return (
                <>
                  <EditAdminDrawerForm id={row.id} onSuccess={refetch} />
                  <HDeletePopconfirmButton onConfirm={() => onDelete(row.id)} disabled={noWrite || row.id === 1} />
                </>
              );
            },
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
