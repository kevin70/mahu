import { SYSTEM_API } from '@/services';
import { useQuery } from '@tanstack/react-query';
import { Transfer } from 'antd';
import { useMemo } from 'react';

export const PermitTransfer = (props: {
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
      }));
    },
  });
  const targetKeys = useMemo(() => {
    return props.value || [];
  }, [props.value]);

  return (
    <Transfer
      dataSource={data}
      listStyle={{
        width: '100%',
        height: 400,
      }}
      titles={['系统权限', '已选择的权限']}
      render={(item) => item.description!}
      showSearch
      targetKeys={targetKeys}
      onChange={(targetKeys) => {
        props.onChange?.(targetKeys.map((o) => o.toString()));
      }}
    ></Transfer>
  );
};
