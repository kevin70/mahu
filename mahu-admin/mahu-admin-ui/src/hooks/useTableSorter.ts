import { SortOrder } from 'antd/lib/table/interface';
import { isArray } from 'lodash-es';
import { useMemo, useState } from 'react';

/**
 * ant-design 表格排序的勾子.
 *
 * @returns
 */
export const useTableSorter = (sorts?: { columnKey: string; order?: SortOrder }[]) => {
  const [tableSorter, setTableSorter] = useState<any>(sorts);
  const querySort: string[] = useMemo(() => {
    const arr = [];
    if (isArray(tableSorter)) {
      tableSorter.forEach((o) => {
        if (o.columnKey) {
          arr.push(o.order === 'ascend' ? o.columnKey : `-${o.columnKey}`);
        }
      });
    } else {
      if (tableSorter && tableSorter.columnKey) {
        arr.push(tableSorter.order === 'ascend' ? tableSorter.columnKey : `-${tableSorter.columnKey}`);
      }
    }
    return arr;
  }, [tableSorter]);

  return { setTableSorter, querySort };
};
