import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useDataFilter, usePagination, useTableSorter } from '@/hooks';
import { BASIS_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, Typography } from 'antd';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { NewBrandDrawerForm } from './NewBrandDrawerForm';

export const BrandList = () => {
  const noWrite = $checkNotPermit(permits.BRAND.W);
  const { pagination, setPagination, setTotal, queryOffsetLimit } = usePagination();
  const { setDataFilters, queryFilter } = useDataFilter();
  const { setTableSorter, querySort } = useTableSorter([{ columnKey: 'ordering' }]);
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/brands', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      const res = await BASIS_API.listBrands(queryOffsetLimit.limit, queryOffsetLimit.offset, queryFilter, querySort);
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
            op: 'icontains',
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
    await BASIS_API.deleteBrand(id);
    $message().success('删除品牌成功');

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
          actions: [<NewBrandDrawerForm onSuccess={refetch} />],
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
          persistenceKey: 'atable_state_brand_list',
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
            title: '字母',
            dataIndex: 'firstLetter',
          },
          {
            title: '名称',
            dataIndex: 'name',
          },
          {
            title: 'LOGO',
            dataIndex: 'logo',
            valueType: 'image',
          },
          {
            title: '排序',
            dataIndex: 'ordering',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => [
              <HDeletePopconfirmButton
                onConfirm={() => onDelete(row.id)}
                description={() => (
                  <div>
                    确认品牌 <Typography.Text mark>{row.name}</Typography.Text>
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
