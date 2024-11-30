import { SYSTEM_API } from '@/services';
import { ProFormSelect, ProFormSelectProps } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';

export const RoleSelect = (props: ProFormSelectProps) => {
  const { data } = useQuery({
    queryKey: ['/system/roles'],
    async queryFn() {
      const res = await SYSTEM_API.listRoles(1000, undefined, undefined, ['-ordering']);
      return res.items;
    },
  });

  const options = useMemo(() => {
    return (data || []).map((o) => ({
      value: o.id,
      label: o.name,
    }));
  }, [data]);
  return <ProFormSelect options={options} {...props} />;
};
