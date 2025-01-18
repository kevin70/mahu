import { useRSQLFilter } from '@/hooks';
import { useTableHelper } from '@/hooks/useTableHelper';
import { MART_API } from '@/services';
import { ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, Modal, Table } from 'antd';
import { HSearchButton } from '../HSearchButton';
import { cloneElement, useState } from 'react';

interface HAttributeChooseProps {
  trigger: JSX.Element;
  value?: number[];
  onChange?: (value: number[]) => void;
}

export const HAttributeChoose = (props: HAttributeChooseProps) => {
  const [open, setOpen] = useState(false);
  const triggerDom = cloneElement(props.trigger, {
    onClick() {
      setOpen(true);
    },
  });

  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'ordering', order: 'descend' }],
  });

  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['MarketAttributeList', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return MART_API.listMartAttributes({
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
        setRSQLFilters([rsqlOps.comparisonEx('name', '=icontains=', values.searchName)]);
      }}
    >
      <ProFormText name="searchName" label="名称" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const [selectedKeys, setSelectedKeys] = useState<number[]>([]);

  return (
    <>
      {triggerDom}
      <Modal
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => {
          setOpen(false);

          props.onChange?.(selectedKeys);
        }}
        width={'80%'}
      >
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
          }}
          loading={isFetching}
          dataSource={data?.items}
          pagination={{ ...pagination, total: data?.totalCount }}
          onChange={onTableChange}
          rowSelection={{
            type: 'checkbox',
            selectedRowKeys: selectedKeys,
            onChange(selectedRowKeys, selectedRows, info) {
              setSelectedKeys(selectedRowKeys as number[]);
            },
          }}
          rowKey={'id'}
          columns={[
            {
              title: 'ID',
              dataIndex: 'id',
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
              title: '排序',
              dataIndex: 'ordering',
              sorter: true,
              defaultSortOrder: 'descend',
            },
            {
              title: '值类型',
              dataIndex: 'valueType',
              valueEnum: {
                INPUT: {
                  text: '输入',
                  status: 'default',
                },
                SELECT: {
                  text: '选择',
                  status: 'success',
                },
              },
            },
          ]}
          expandable={{
            rowExpandable: (record) => record.valueType === 'SELECT',
            expandedRowRender(row) {
              const dataSource = row.attributeValues ?? [];
              return (
                <Table
                  pagination={false}
                  size="small"
                  columns={[
                    {
                      title: '值',
                      dataIndex: 'value',
                    },
                    {
                      title: '排序',
                      dataIndex: 'ordering',
                    },
                  ]}
                  dataSource={dataSource}
                />
              );
            },
          }}
        ></ProTable>
      </Modal>
    </>
  );
};
