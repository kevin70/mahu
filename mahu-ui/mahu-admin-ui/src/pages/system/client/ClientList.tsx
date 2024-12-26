import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useDataFilter, usePagination, useTableSorter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, Typography } from 'antd';
import { NewClientDrawerForm } from './NewClientDrawerForm';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { EditClientDrawerForm } from './EditClientDrawerForm';

export const ClientList = () => {
  const noWrite = $checkNotPermit(permits.CLIENT.W);
  const { pagination, setPagination, setTotal, queryOffsetLimit } = usePagination();
  const { setDataFilters, queryFilter } = useDataFilter();
  const { setTableSorter, querySort } = useTableSorter([{ columnKey: 'create_time' }]);
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/clients', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      const res = await SYSTEM_API.listClients(queryOffsetLimit.limit, queryOffsetLimit.offset, queryFilter, querySort);
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
            qname: 'client_id',
            op: 'eq',
            value: values.clientId,
          },
        ]);
      }}
    >
      <ProFormText name="clientId" label="客户端 ID" placeholder="客户端 ID" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (clientId: string) => {
    await SYSTEM_API.deleteClient(clientId);
    $message().success('删除认证客户端成功');

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
          actions: [<NewClientDrawerForm onSuccess={refetch} />],
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
            title: '客户端 ID',
            dataIndex: 'clientId',
            copyable: true,
            ellipsis: true,
          },
          {
            key: 'create_time',
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
            sorter: true,
            defaultSortOrder: 'descend',
          },
          {
            key: 'update_time',
            title: '更新时间',
            dataIndex: 'updateTime',
            valueType: 'dateTime',
            sorter: true,
          },
          {
            title: '客户端密钥',
            dataIndex: 'clientSecret',
            valueType: 'password',
          },
          {
            title: '标签',
            dataIndex: 'label',
          },
          {
            title: '备注',
            dataIndex: 'remark',
            ellipsis: true,
          },
          {
            title: '终端类型',
            dataIndex: 'terminalType',
          },
          {
            title: '微信 APPID',
            dataIndex: 'wechatAppid',
          },
          {
            title: '微信 APP 密钥',
            dataIndex: 'wechatAppsecret',
            valueType: 'password',
          },
          {
            title: '操作',
            align: 'right',
            fixed: 'right',
            render: (_dom, row) => [
              <EditClientDrawerForm clientId={row.clientId} onSuccess={refetch} />,
              <HDeletePopconfirmButton
                onConfirm={() => onDelete(row.clientId)}
                description={() => (
                  <div>
                    确认删除认证终端 <Typography.Text mark>{row.clientId}</Typography.Text>{' '}
                    <Typography.Text mark>{row.label}</Typography.Text>
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
