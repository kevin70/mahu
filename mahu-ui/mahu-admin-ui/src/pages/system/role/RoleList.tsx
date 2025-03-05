import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, message } from 'antd';
import { NewRoleDrawerForm } from './NewRoleDrawerForm';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { EditRoleDrawerForm } from './EditRoleDrawerForm';
import { useTableHelper } from '@/hooks/useTableHelper';

export const RoleList = () => {
  const noWrite = $checkNotPermit(permits.ROLE.W);
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'ordering', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/roles', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listRoles({
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
        setRSQLFilters([rsqlOps.comparisonEx('name', '=icontains=', values.name)]);
      }}
    >
      <ProFormText name="name" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await SYSTEM_API.deleteRole({ id });
    message.success('删除角色成功');
    await refetch();
  };

  const isInsideData = (row: any) => {
    return row.id === 1;
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
          actions: [<NewRoleDrawerForm onSuccess={refetch} />],
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
          },
          {
            title: '名称',
            dataIndex: 'name',
          },
          {
            title: '备注',
            dataIndex: 'remark',
            ellipsis: true,
          },
          {
            key: 'ordering',
            title: '排序',
            dataIndex: 'ordering',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => {
              if (isInsideData(row)) {
                return <></>;
              }
              return (
                <>
                  <EditRoleDrawerForm id={row.id} onSuccess={refetch} />
                  <HDeletePopconfirmButton onConfirm={() => onDelete(row.id)} disabled={noWrite} />
                </>
              );
            },
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
