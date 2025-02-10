import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { HIncludeDetedCheckBox } from '@/components/HIncludeDeletedCheckbox';
import { HNewButton } from '@/components/HNewButton';
import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { MART_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { Avatar, Form, message, Table } from 'antd';
import { useState } from 'react';
import { Link } from 'react-router';

export const MartProductList = () => {
  const noWrite = $checkNotPermit(permits.MART_PRODUCT.W);

  const [incldeDeleted, setIncludeDeleted] = useState<number | undefined>();
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'ordering', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['MartProductList', queryOffsetLimit, queryFilter, querySort, incldeDeleted],
    async queryFn() {
      return MART_API.listMartProducts({
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
          rsqlOps.comparisonEx('slug', '==', values.searchSlug),
          rsqlOps.comparisonEx('name', '=icontains=', values.searchName),
        ]);
      }}
    >
      <ProFormText name="searchName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (id: number) => {
    // await MART_API.deleteMartAttribute({ id });
    message.success('删除成功');
    refetch();
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
          actions: [
            <Link key={'product-new'} to={'/mart/product-new'}>
              <HNewButton />
            </Link>,
          ],
          filter: <HIncludeDetedCheckBox onChange={(e) => setIncludeDeleted(e.target.checked ? 1 : undefined)} />,
        }}
        loading={isFetching}
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
        columnsState={{
          persistenceType: 'localStorage',
          persistenceKey: 'atable.state.MartProductList',
        }}
        rowKey={'id'}
        columns={[
          {
            title: 'SPU',
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
            title: '名称',
            dataIndex: 'name',
          },
          {
            title: '描述',
            dataIndex: 'description',
            ellipsis: true,
          },
          {
            title: '类型',
            dataIndex: 'type',
            valueEnum: {
              PHYSICAL: '实体',
              VIRTUAL: '虚拟',
            },
          },
          {
            title: '图片',
            render(_dom, row) {
              return (
                <Avatar.Group shape="square">
                  {row.images.map((uri) => (
                    <Avatar key={uri} src={uri} />
                  ))}
                </Avatar.Group>
              );
            },
          },
          {
            title: '状态',
            dataIndex: 'status',
            valueEnum: {
              PENDING: {
                text: '待定',
                status: 'warning',
              },
              ACTIVE: {
                text: '上架',
                status: 'success',
              },
              INACTIVE: {
                text: '下架',
                status: 'error',
              },
              DRAFT: {
                text: '草稿',
                status: 'default',
              },
            },
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render(_dom, row) {
              if (row.deleted) {
                return <></>;
              }
              return (
                <>
                  <HDeletePopconfirmButton onConfirm={() => onDelete(row.id!)} disabled={noWrite} />
                </>
              );
            },
          },
        ]}
        expandable={{
          rowExpandable(row) {
            return (row.variants?.length || 0) > 0;
          },
          expandedRowRender(row) {
            const dataSource = row.variants ?? [];
            return (
              <ProTable
                search={false}
                options={false}
                pagination={false}
                size="small"
                columns={[
                  {
                    title: 'SKU',
                    dataIndex: 'id',
                  },
                  {
                    title: '限定名',
                    dataIndex: 'qn',
                  },
                  {
                    title: '封面',
                    dataIndex: 'cover',
                    valueType: 'image',
                  },
                  {
                    title: '价格',
                    dataIndex: 'price',
                    valueType: 'money',
                  },
                  {
                    title: '长(cm)',
                    dataIndex: 'length',
                  },
                  {
                    title: '宽(cm)',
                    dataIndex: 'width',
                  },
                  {
                    title: '高(cm)',
                    dataIndex: 'height',
                  },
                  {
                    title: '重量(g)',
                    dataIndex: 'weight',
                  },
                  {
                    title: '状态',
                    dataIndex: 'status',
                    valueEnum: {
                      PENDING: {
                        text: '待定',
                        status: 'warning',
                      },
                      ACTIVE: {
                        text: '上架',
                        status: 'success',
                      },
                      INACTIVE: {
                        text: '下架',
                        status: 'error',
                      },
                      DRAFT: {
                        text: '草稿',
                        status: 'default',
                      },
                    },
                  },
                  {
                    title: '操作',
                    align: 'right',
                    render(_dom, subRow) {
                      if (row.deleted || subRow.deleted) {
                        return <></>;
                      }
                      return <></>;
                    },
                  },
                ]}
                dataSource={dataSource}
              />
            );
          },
        }}
      ></ProTable>
    </PageContainer>
  );
};
