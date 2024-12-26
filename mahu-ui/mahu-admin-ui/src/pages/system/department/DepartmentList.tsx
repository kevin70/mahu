import { useDataFilter, usePagination, useTableSorter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, Typography } from 'antd';
import { NewDepartmentDrawerForm } from './NewDepartmentDrawerForm';
import { HSearchButton } from '@/components/HSearchButton';
import { EditDepartmentDrawerForm } from './EditDepartmentDrawerForm';
import { permits } from '@/config/permit';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';

export const DepartmentList = () => {
  const noWrite = $checkNotPermit(permits.DEPARTMENT.W);
  const { pagination, setPagination, setTotal, queryOffsetLimit } = usePagination();
  const { setDataFilters, queryFilter } = useDataFilter();
  const { setTableSorter, querySort } = useTableSorter([{ columnKey: 'ordering' }]);
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/departments', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      const res = await SYSTEM_API.listDepartments(
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
            qname: 'name',
            op: 'contains',
            value: values.searchName,
          },
        ]);
      }}
    >
      <ProFormText name="searchName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    await SYSTEM_API.deleteDepartment(id);
    $message().success('删除部门成功');

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
            render: (_dom, row) => [
              <EditDepartmentDrawerForm id={row.id} onSuccess={refetch} />,
              <HDeletePopconfirmButton
                onConfirm={() => onDelete(row.id)}
                description={() => (
                  <div>
                    确认删除部门 <Typography.Text mark>{row.name}</Typography.Text>
                  </div>
                )}
                disabled={noWrite}
              />,
            ],
          },
        ]}
      ></ProTable>
    </PageContainer>
  );
};
