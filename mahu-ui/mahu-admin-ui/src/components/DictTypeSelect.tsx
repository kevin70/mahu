import { BASE_API } from '@/services';
import { useQuery } from '@tanstack/react-query';
import { Flex, Select, SelectProps } from 'antd';
import { useMemo } from 'react';

/**
 * 字典类型选择器
 */
export const DictTypeSelect = (props: SelectProps) => {
  const { data } = useQuery({
    queryKey: ['/dicts'],
    async queryFn() {
      const res = await BASE_API.listDicts();
      return res;
    },
  });
  const options = useMemo(() => {
    return (
      data?.map((o) => {
        return { ...o, value: o.typeCode };
      }) || []
    );
  }, [data]);

  return (
    <Select
      placeholder={'请选择'}
      {...props}
      options={options}
      filterOption={(input, option) => {
        const s = input.toLowerCase();
        return !!(option?.typeCode.toLowerCase().includes(s) || option?.name.toLowerCase().includes(s));
      }}
      optionRender={(o) => {
        return (
          <Flex justify="space-between">
            <div>{o.data.typeCode}</div>
            <div>{o.data.name}</div>
          </Flex>
        );
      }}
    ></Select>
  );
};
