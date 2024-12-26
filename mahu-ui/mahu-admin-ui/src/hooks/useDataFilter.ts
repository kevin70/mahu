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

  /// 更新删除数据过滤器
  const upsertDataFilters = (filters: Filter[]) => {
    const arr = [...filters];
    for (const f of dataFilters) {
      const idx = arr.findIndex((o) => o.qname === f.qname);
      if (idx === -1) {
        arr.push(f);
      }
    }
    return setDataFilters(arr);
  };

  /// 删除数据过滤
  const removeDataFilters = (qnames: string[]) => {
    const filters = dataFilters.filter((o) => qnames.includes(o.qname));
    setDataFilters(filters);
  };

  return { setDataFilters, upsertDataFilters, removeDataFilters, queryFilter };
};
