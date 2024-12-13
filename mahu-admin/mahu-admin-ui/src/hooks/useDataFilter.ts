import { useMemo, useState } from 'react';
import dayjs from 'dayjs';

type Filter = {
  value: unknown;
  qname: string;
  op: 'eq' | 'ne' | 'gt' | 'lt' | 'gte' | 'lte' | 'contains' | 'icontains' | 'in' | 'nin' | 'between';
};

/**
 * 数据过滤.
 */
export const useDataFilter = () => {
  const [dataFilters, setDataFilters] = useState<Filter[]>([]);

  const queryFilter = useMemo(() => {
    const arr = [];
    for (const element of dataFilters) {
      if (element.value) {
        let v;
        if (element.value instanceof String) {
          v = element.value.trim();
        } else if (element.value instanceof Date) {
          v = dayjs(element.value).format('YYYY-MM-DDTHH:mm:ss');
        } else {
          v = new String(element.value).trim();
        }

        if (v && v !== '') {
          arr.push(`${element.qname} ${element.op} ${v}`);
        }
      }
    }

    return arr;
  }, [dataFilters]);

  return { setDataFilters, queryFilter };
};
