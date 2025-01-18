import { useRSQLFilter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, message } from 'antd';
import { NewDepartmentDrawerForm } from './NewDepartmentDrawerForm';
import { HSearchButton } from '@/components/HSearchButton';
import { EditDepartmentDrawerForm } from './EditDepartmentDrawerForm';
import { permits } from '@/config/permit';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { useTableHelper } from '@/hooks/useTableHelper';

export const DepartmentList = () => {
  const noWrite = $checkNotPermit(permits.DEPARTMENT.W);
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'ordering' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/departments', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listDepartments({
        ...queryOffsetLimit,
        filter: queryFilter,
        sort: querySort,
      });
    },
  });

  const searchForm = (
    <Form
      layout="inline"
      onFinish={(values: any) => {
        gotoFirstPage();
        setRSQLFilters([rsqlOps.comparisonEx('name', '=icontains=', values.searchName)]);
      }}
    >
      <ProFormText name="searchName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await SYSTEM_API.deleteDepartment({ id });
    message.success('删除部门成功');

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
          actions: [<NewDepartmentDrawerForm onSuccess={refetch} />],
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
          },
          {
            title: '名称',
            dataIndex: 'name',
          },
          {
            title: '父部门',
            dataIndex: ['parent', 'name'],
          },
          {
            title: '排序',
            dataIndex: 'ordering',
            key: 'ordering',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => {
              return (
                <>
                  <EditDepartmentDrawerForm id={row.id} onSuccess={refetch} />
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
