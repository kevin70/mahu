import { HSearchButton } from '@/components/HSearchButton';
import { permits } from '@/config/permit';
import { useRSQLFilter } from '@/hooks';
import { SYSTEM_API } from '@/services';
import { PageContainer, ProFormText, ProTable } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { Form, message, Typography } from 'antd';
import { NewClientDrawerForm } from './NewClientDrawerForm';
import { HDeletePopconfirmButton } from '@/components/HDeletePopconfirmButton';
import { EditClientDrawerForm } from './EditClientDrawerForm';
import { useTableHelper } from '@/hooks/useTableHelper';

export const ClientList = () => {
  const noWrite = $checkNotPermit(permits.CLIENT.W);
  const { onTableChange, pagination, gotoFirstPage, queryOffsetLimit, querySort } = useTableHelper({
    sort: [{ columnKey: 'create_time', order: 'descend' }],
  });
  const { setRSQLFilters, rsqlOps, queryFilter } = useRSQLFilter();
  const { data, isFetching, refetch } = useQuery({
    queryKey: ['/system/clients', queryOffsetLimit, queryFilter, querySort],
    async queryFn() {
      return SYSTEM_API.listClients({
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
        setRSQLFilters([rsqlOps.comparisonEx('client_id', '==', values.clientId)]);
      }}
    >
      <ProFormText name="clientId" label="客户端 ID" placeholder="客户端 ID" />
      <HSearchButton type="primary" htmlType="submit" loading={isFetching} />
    </Form>
  );

  const onDelete = async (clientId: string) => {
    await SYSTEM_API.deleteClient({ clientId });
    message.success('删除认证客户端成功');

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
        dataSource={data?.items}
        pagination={{ ...pagination, total: data?.totalCount }}
        onChange={onTableChange}
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
