import { SYSTEM_API } from '@/services';
import { ProFormSelect, ProFormSelectProps } from '@ant-design/pro-components';
import { useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';

export const RoleSelect = (props: ProFormSelectProps) => {
  const { data } = useQuery({
    queryKey: ['/system/roles'],
    async queryFn() {
      const res = await SYSTEM_API.listRoles({ limit: 1000, sort: ['-ordering'] });
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
