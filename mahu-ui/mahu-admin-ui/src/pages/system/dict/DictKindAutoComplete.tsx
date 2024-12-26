import { SYSTEM_API } from '@/services';
import { useQuery } from '@tanstack/react-query';
import { AutoComplete, AutoCompleteProps, Space, Typography } from 'antd';
import { useMemo } from 'react';

export const DictKindAutoComplete = (props: AutoCompleteProps) => {
  const { data } = useQuery({
    queryKey: ['DictKindAutoComplete'],
    async queryFn() {
      return SYSTEM_API.listDictPredefineKinds();
    },
  });

  const items = useMemo(() => {
    return (
      data?.map((o) => ({
        value: o.kind,
        label: o.label,
      })) || []
    );
  }, [data]);

  return (
    <AutoComplete
      options={items}
      optionRender={(o) => {
        return (
          <Space size={'large'}>
            <Typography.Text>{o.value}</Typography.Text>
            <Typography.Text type="secondary">{o.label}</Typography.Text>
          </Space>
        );
      }}
      {...props}
    />
  );
};
