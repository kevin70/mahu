import { SYSTEM_API } from '@/services';
import { useQuery } from '@tanstack/react-query';
import { TreeSelect, TreeSelectProps } from 'antd';
import { useMemo } from 'react';

export const DepartmentTreeSelect = ({
  nonSelectIds,
  ...otherProps
}: TreeSelectProps & {
  nonSelectIds?: number[];
}) => {
  const { data } = useQuery({
    queryKey: ['/system/departments'],
    async queryFn() {
      const res = await SYSTEM_API.listDepartments(5000);
      return res.items;
    },
  });

  const items = useMemo(() => {
    const all = data || [];
    const list: any[] = [];

    const arrange = (parent?: any) => {
      const pid = parent?.id;
      for (const department of all) {
        const parentId = department.parent?.id;
        if (pid === parentId) {
          const disabled = (nonSelectIds || []).indexOf(department.id) >= 0;
          const item = {
            id: department.id,
            value: department.id,
            title: department.name,
            children: [],
            disabled: disabled || parent?.disabled,
          };

          if (parent == null) {
            list.push(item);
          } else {
            const children = parent.children || [];
            children.push(item);
          }
          arrange(item);
        }
      }
    };

    arrange();
    return list;
  }, [data, nonSelectIds]);

  return <TreeSelect treeData={items} {...otherProps}></TreeSelect>;
};
