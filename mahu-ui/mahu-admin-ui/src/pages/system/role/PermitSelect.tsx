import { SYSTEM_API } from '@/services';
import { ProTable } from '@ant-design/pro-components';
import { css } from '@styled-system/css';
import { useQuery } from '@tanstack/react-query';
import { Checkbox } from 'antd';

export const PermitSelect = (props: {
  id?: string;
  value?: string[];
  onChange?: (v: string[] | undefined) => void;
}) => {
  const { data } = useQuery({
    queryKey: ['/system/permits'],
    async queryFn() {
      const res = await SYSTEM_API.allPermits();
      return res.map((o) => ({
        key: o.code,
        title: o.code,
        description: o.label,
        canRead: o.canRead,
        canWrite: o.canWrite,
        canDelete: o.canDelete,
      }));
    },
  });

  return (
    <Checkbox.Group value={props.value} onChange={props.onChange}>
      <ProTable
        className={css`
          flex-grow: 1;
        `}
        bordered
        sticky
        search={false}
        toolBarRender={false}
        pagination={false}
        rowKey="key"
        scroll={{ y: 360 }}
        columns={[
          {
            title: '标签',
            dataIndex: 'description',
          },
          {
            title: '代码',
            dataIndex: 'key',
          },
          {
            title: '可读取',
            width: 64,
            align: 'center',
            render(dom, entity, index, action, schema) {
              return <Checkbox disabled={!entity.canRead} value={`${entity.key}:R`} />;
            },
          },
          {
            title: '可写入',
            width: 64,
            align: 'center',
            render(dom, entity, index, action, schema) {
              return <Checkbox disabled={!entity.canWrite} value={`${entity.key}:W`} />;
            },
          },
          {
            title: '可删除',
            width: 64,
            align: 'center',
            render(dom, entity, index, action, schema) {
              return <Checkbox disabled={!entity.canDelete} value={`${entity.key}:D`} />;
            },
          },
        ]}
        dataSource={data}
      ></ProTable>
    </Checkbox.Group>
  );
};
